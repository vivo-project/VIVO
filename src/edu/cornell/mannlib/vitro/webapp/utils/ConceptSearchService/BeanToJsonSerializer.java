package edu.cornell.mannlib.vitro.webapp.utils.ConceptSearchService;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import edu.cornell.mannlib.semservices.bo.Day;
import edu.cornell.mannlib.semservices.bo.Time;
import edu.cornell.mannlib.semservices.util.ClassUtils;
import edu.cornell.mannlib.semservices.util.DateConverter;
import edu.cornell.mannlib.semservices.util.DayConverter;
import edu.cornell.mannlib.semservices.util.ObjectUtils;
import edu.cornell.mannlib.semservices.util.TimeConverter;
import edu.cornell.mannlib.semservices.util.TimestampConverter;

@SuppressWarnings("serial")
public class BeanToJsonSerializer {
   private static final Log logger = LogFactory.getLog(BeanToJsonSerializer.class);

   /**
    *
    */
   private BeanToJsonSerializer() {
      ConvertUtils.register(new DateConverter(), java.util.Date.class);
      ConvertUtils.register(new TimestampConverter(),
            String.class);
      ConvertUtils.register(new TimeConverter(), String.class);
      ConvertUtils.register(new DayConverter(), String.class);
   }

   /**
    *
    */
   @SuppressWarnings("unchecked")
   private static final Set<Class> CONVERTABLE_CLASSES = new HashSet<Class>() {
      {
         add(XMLGregorianCalendar.class);
      }
   };

   /**
    * @param bean
    * @return
    */
   public static JSONObject serializeToJsonObject(Object bean) {
      Object result = serialize(bean);
      JSONObject jsonObj = new JSONObject();
      try {
         if (ObjectUtils.isArray(bean) || result instanceof JSONArray) {
            jsonObj.put("array", result);
         } else if (result instanceof JSONObject) {
            jsonObj = (JSONObject) result;
         } else {
            jsonObj.put(bean.getClass().getSimpleName(), result);
         }
      } catch (JSONException e) {
         logger.error("JSONException ",e);
      }
      return jsonObj;
   }

   /**
    * @param bean
    * @return
    */
   @SuppressWarnings("unchecked")
   public static Object serialize(Object bean) {
      //String simpleName = bean.getClass().getSimpleName();
      Object result = JSONNull.getInstance();

      if (isObjectJson(bean)) {
         result = bean;
      } else if (bean != null && isConvertable(bean.getClass())) {
         //logger.info("Converting convertable Class: "+simpleName);
         result = ConvertUtils.convert(bean);
      } else if (bean != null && Time.class.isAssignableFrom(bean.getClass())) {
         //logger.info("Converting Time Class: "+simpleName);
         result = TimeConverter.toUnixTime((Time) bean);
      } else if (bean != null && Day.class.isAssignableFrom(bean.getClass())) {
         //logger.info("Converting Day Class: "+simpleName);
         result = DayConverter.toUnixTime((Day) bean);
      } else if (bean != null
            && java.util.Date.class.isAssignableFrom(bean.getClass())) {
         // for date consistency, use the XMLGregorianCalendar
         // result =
         // DateConverter.toXMLGregorianCalendar((java.util.Date)bean
         // ).toString();
         //logger.info("Converting Date Class: "+simpleName);
         result = DateConverter.toFormattedString((java.util.Date) bean);
      } else if (bean != null && ObjectUtils.isComplex(bean)
            && !ObjectUtils.isArray(bean) && !ObjectUtils.isMap(bean)) {
         //logger.info("Converting complex bean: "+simpleName);
         JSONObject jsonObject = new JSONObject();
         try {
            PropertyDescriptor[] pds = PropertyUtils
                  .getPropertyDescriptors(bean);
            for (int i = 0; i < pds.length; i++) {
               String key = pds[i].getName();
               if ("class".equals(key)) {
                  continue;
               }

               Class type = pds[i].getPropertyType();
               Object value = PropertyUtils.getProperty(bean, key);

               if (String.class.isAssignableFrom(type)) {
                  jsonObject.put(key, (value == null) ? "" : value);
               } else if (ObjectUtils.isArray(value)) {
                  jsonObject.put(key, serialize(value));
               } else if (value == null) {
                  jsonObject.put(key, JSONNull.getInstance());
               } else if (ObjectUtils.isSimpleType(value) || type.isEnum()) {
                  if (ClassUtils.isXmlEnum(type)) {
                     jsonObject.put(key, ObjectUtils.getXmlEnumValue(value));
                  } else {
                     jsonObject.put(key, value);
                  }
               } else {
                  jsonObject.put(key, serialize(value));
               }
            }

            result = jsonObject;
         } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException ", e);
         } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException ", e);
         } catch (NoSuchMethodException e) {
            logger.error("NoSuchMethodException ", e);
         } catch (JSONException e) {
            logger.error("JSONException ", e);
         }
      } else if (ObjectUtils.isArray(bean)) {
         //logger.info("Converting Array bean: "+simpleName);
         Collection collection = null;
         if (bean.getClass().isArray()) {
            collection = Arrays.asList((Object[]) bean);
         } else {
            collection = (Collection) bean;
         }

         result = new JSONArray();
         for (Object item : collection) {
            ((JSONArray) result).add(serialize(item));
         }
      } else if (ObjectUtils.isMap(bean)) {
         //logger.info("Converting Map bean: "+simpleName);
         Map map = (Map) bean;
         result = new JSONObject();
         for (Object key : map.keySet()) {
            try {
               ((JSONObject) result).put(key.toString(),
                     serialize(map.get(key)));
            } catch (JSONException e) {
               logger.error("JSONException ",e);
            }
         }
      } else if (bean != null && ClassUtils.isXmlEnum(bean.getClass())) {
         //logger.info("converting xmlEnum bean: "+simpleName);
         result = ObjectUtils.getXmlEnumValue(bean);
      } else {
         //logger.info("just returning the bean: "+simpleName);
         result = bean;
      }
      return result;
   }

   /**
    * @param clazz
    * @return
    */
   @SuppressWarnings("unchecked")
   private static boolean isConvertable(Class clazz) {
      boolean found = false;
      for (Class convertableClass : CONVERTABLE_CLASSES) {
         if (convertableClass.isAssignableFrom(clazz)) {
            found = true;
            break;
         } else {
            //logger.warn("Class is not convertable");
            //logger.warn("Class: " + clazz.getSimpleName());
         }
      }
      return found;
   }

   /**
    * @param bean
    * @return
    */
   private static boolean isObjectJson(Object bean) {
      if ((JSONNull.getInstance().equals(bean)) || (bean instanceof JSONObject)
            || (bean instanceof JSONArray)) {
         return true;
      }
      return false;
   }
}
