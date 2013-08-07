package edu.cornell.mannlib.semservices.bo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SparqlFeed extends BaseObject implements Serializable {


   /**
    *
    */
   private static final long serialVersionUID = 6024170338796859330L;
   private String name;
   private String endpoint;
   private String repository;
   private String query;
   private int mapperId;
   private int fetcherId;
   private Map<String, String> parameterMap;

   /**
    * Constructor
    */
   public SparqlFeed() {
      super();
      parameterMap = new HashMap<String, String>();
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the endpoint
    */
   public String getEndpoint() {
      return endpoint;
   }

   /**
    * @param endpoint the endpoint to set
    */
   public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
   }

   /**
    * @return the repository
    */
   public String getRepository() {
      return repository;
   }

   /**
    * @param repository the repository to set
    */
   public void setRepository(String repository) {
      this.repository = repository;
   }

   /**
    * @return the query
    */
   public String getQuery() {
      return query;
   }

   /**
    * @param query the query to set
    */
   public void setQuery(String query) {
      this.query = query;
   }

   /**
    * @return the mapperId
    */
   public int getMapperId() {
      return mapperId;
   }

   /**
    * @param mapperId the mapperId to set
    */
   public void setMapperId(int mapperId) {
      this.mapperId = mapperId;
   }

   /**
    * @return the fetcherId
    */
   public int getFetcherId() {
      return fetcherId;
   }

   /**
    * @param fetcherId the fetcherId to set
    */
   public void setFetcherId(int fetcherId) {
      this.fetcherId = fetcherId;
   }

   /**
    * @return the parameterMap
    */
   public Map<String, String> getParameterMap() {
      return parameterMap;
   }

   /**
    * @param parameterMap the parameterMap to set
    */
   public void setParameterMap(Map<String, String> parameterMap) {
      this.parameterMap = parameterMap;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
      result = prime * result + fetcherId;
      result = prime * result + mapperId;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result
            + ((parameterMap == null) ? 0 : parameterMap.hashCode());
      result = prime * result + ((query == null) ? 0 : query.hashCode());
      result = prime * result
            + ((repository == null) ? 0 : repository.hashCode());
      return result;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      SparqlFeed other = (SparqlFeed) obj;
      if (endpoint == null) {
         if (other.endpoint != null) {
            return false;
         }
      } else if (!endpoint.equals(other.endpoint)) {
         return false;
      }
      if (fetcherId != other.fetcherId) {
         return false;
      }
      if (mapperId != other.mapperId) {
         return false;
      }
      if (name == null) {
         if (other.name != null) {
            return false;
         }
      } else if (!name.equals(other.name)) {
         return false;
      }
      if (parameterMap == null) {
         if (other.parameterMap != null) {
            return false;
         }
      } else if (!parameterMap.equals(other.parameterMap)) {
         return false;
      }
      if (query == null) {
         if (other.query != null) {
            return false;
         }
      } else if (!query.equals(other.query)) {
         return false;
      }
      if (repository == null) {
         if (other.repository != null) {
            return false;
         }
      } else if (!repository.equals(other.repository)) {
         return false;
      }
      return true;
   }



}
