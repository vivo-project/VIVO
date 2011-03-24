package edu.cornell.mannlib.vitro.webapp.controller.edit;

/* $This file is distributed under the terms of the license in /doc/license.txt$ */
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.ontology.OntModel;

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vitro.webapp.beans.User;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroHttpServlet;
import edu.cornell.mannlib.vitro.webapp.dao.UserDao;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.dao.jena.LoginEvent;
import edu.cornell.mannlib.vitro.webapp.dao.jena.LoginLogoutEvent;

/*
 * yxl: This is a copy of Authenticate.java and modified for Shibboleth authentication
 * 
 */
public class ShibauthAdminAuthenticate extends VitroHttpServlet  {
    private static final int DEFAULT_PORTAL_ID=1;
    public static final String USER_SESSION_MAP_ATTR = "userURISessionMap";
    private UserDao userDao = null;
    private static final Log log = LogFactory.getLog(Authenticate.class.getName());

    public void doPost( HttpServletRequest request, HttpServletResponse response ) {
        try {
            HttpSession session = request.getSession();
            if(session.isNew()){
                session.setMaxInactiveInterval(300); // seconds, not milliseconds
            }
            userDao = ((WebappDaoFactory)session.getServletContext().getAttribute("webappDaoFactory")).getUserDao();
            LoginFormBean f = (LoginFormBean) session.getAttribute( "loginHandler" );

            //obtain a db connection and perform a db query
            //ensuring that the username exists

            // JCR 20040905 passing on portal home parameter
            String portalIdStr=(portalIdStr=request.getParameter("home"))==null?String.valueOf(DEFAULT_PORTAL_ID):portalIdStr;
            //request.setAttribute("home",portalIdStr);

            // Build the redirect URLs
            String contextPath = request.getContextPath();
            String urlParams = "?home=" + portalIdStr + "&login=block";
            String loginUrl = contextPath + Controllers.LOGIN + urlParams;
            String siteAdminUrl = contextPath + Controllers.SITE_ADMIN + urlParams;

            if (userDao==null) {
                f.setErrorMsg("loginPassword","unable to get UserDao");
                f.setLoginStatus("no UserDao");
                response.sendRedirect(loginUrl);
                return;
            }

            /* used for encoding cleartext passwords sent via http before store in database
            String loginPassword = "";
            String passwordQuery = "SELECT PASSWORD('" + f.getLoginPassword() + "')";
            ResultSet ps = stmt.executeQuery( passwordQuery );
            while ( ps.next() ) {
                loginPassword = ps.getString(1);
            }
            */
            String userEnteredPasswordAfterMd5Conversion=f.getLoginPassword(); // won't be null
            if ( userEnteredPasswordAfterMd5Conversion.equals("") ) { // shouldn't get through JS form verification
                f.setErrorMsg( "loginPassword","please enter a password" );
                f.setLoginStatus("bad_password");
                response.sendRedirect(loginUrl);
                return;
            }

            User user = userDao.getUserByUsername(f.getLoginName());

            if (user==null) {
                f.setErrorMsg( "loginName","No user found with username " + f.getLoginName() );
                f.setLoginStatus("unknown_username");
                response.sendRedirect(loginUrl);
                return;
            }

            // logic for authentication
            // first check for new users (loginCount==0)
            //   1) cold (have username but haven't received initial password)
            //   2) initial password has been set but user mis-typed it
            //   3) correctly typed initial password and oldpassword set to provided password; have to enter a different one
            //   4) entered same password again
            //   5) entered a new private password, and bypass this stage because logincount set to 1
            // then check for users DBA has set to require changing password (md5password is null, oldpassword is not)
            //
            // check password; dbMd5Password is md5password from database
            if (user.getLoginCount() == 0 ) { // new user
                if ( user.getMd5password() == null ) { // user is known but has not been given initial password
                    f.setErrorMsg( "loginPassword", "Please request a username and initial password via the link below" ); // store password in database but force immediate re-entry
                    f.setLoginStatus("first_login_no_password");
                } else if (!user.getMd5password().equals( userEnteredPasswordAfterMd5Conversion )) { // mis-typed CCRP-provided initial password
                    if ( user.getOldPassword() == null ) { // did not make it through match of initially supplied password
                        f.setErrorMsg( "loginPassword", "Please try entering provided password again" );
                        f.setLoginStatus("first_login_mistyped");
                    } else if (user.getOldPassword().equals( userEnteredPasswordAfterMd5Conversion ) ) {
                        f.setErrorMsg( "loginPassword", "Please pick a different password from initially provided one" );
                        f.setLoginStatus("changing_password_repeated_old");
                    } else { // successfully provided different, private password
                        f.setErrorMsg( "loginPassword", "Please re-enter new private password" );
                        user.setMd5password(userEnteredPasswordAfterMd5Conversion);
                        user.setLoginCount(1);
                        userDao.updateUser(user);
                        f.setLoginStatus("changing_password");
                    }
                } else { // entered a password that matches initial md5password in database; now force them to change it
                    // oldpassword could be null or not null depending on number of mistries
                    f.setErrorMsg( "loginPassword", "Please now choose a private password" ); // store password in database but force immediate re-entry
                    user.setOldPassword(user.getMd5password());
                    userDao.updateUser(user);
                    f.setLoginStatus("first_login_changing_password");
                }
                response.sendRedirect(loginUrl);
                return;
            } else if ( user.getMd5password()==null ) { // DBA has forced entry of a new password for user with a loginCount > 0
                if ( user.getOldPassword() != null && user.getOldPassword().equals( userEnteredPasswordAfterMd5Conversion ) ) {
                    f.setErrorMsg( "loginPassword", "Please pick a different password from your old one" );
                    f.setLoginStatus("changing_password_repeated_old");
                } else {
                    f.setErrorMsg( "loginPassword", "Please re-enter new password" );
                    user.setMd5password(userEnteredPasswordAfterMd5Conversion);
                    userDao.updateUser(user);
                    f.setLoginStatus("changing_password");
                }
                response.sendRedirect(loginUrl);
                return;
            } else if (!user.getMd5password().equals( userEnteredPasswordAfterMd5Conversion )) {
                /*
                 *  yxl: comment out the following code so that Shib can login an admin user
                 *  without using a password as long as the glid existed in the "user" table. 
                 */
                
                /*
                f.setErrorMsg( "loginPassword", "Incorrect password: try again");
                f.setLoginStatus("bad_password");
                f.setLoginPassword(""); // don't even reveal how many characters there were
                response.sendRedirect(loginUrl);
                return;
                */
            }

            //set the login bean properties from the database

            //System.out.println("authenticated; setting login status in loginformbean");

            f.setUserURI(user.getURI());
            f.setLoginStatus( "authenticated" );
            f.setSessionId( session.getId());
            f.setLoginRole( user.getRoleURI() );
            try {
                int loginRoleInt = Integer.decode(f.getLoginRole());
                if( (loginRoleInt>1) && (session.isNew()) ) {
                    session.setMaxInactiveInterval(32000); // set longer timeout for editors
                }
            } catch (Exception e) {}
            // TODO : might be a problem in next line - no ID
            f.setLoginUserId( -2 );
            //f.setEmailAddress ( email );
            f.setLoginPassword( "" );
            f.setErrorMsg( "loginPassword", "" ); // remove any error messages
            f.setErrorMsg( "loginUsername", "" );

            //System.out.println("updating loginCount and modTime");
            
            Map<String,HttpSession> userURISessionMap = getUserURISessionMapFromContext( getServletContext() );
            userURISessionMap.put( user.getURI(), request.getSession() );
            
            sendLoginNotifyEvent(new LoginEvent( user.getURI() ), getServletContext(), session);                
                
            user.setLoginCount(user.getLoginCount()+1);
            userDao.updateUser(user);

            if ( user.getLoginCount() == 2 ) { // first login
                Calendar cal = Calendar.getInstance();
                user.setFirstTime(cal.getTime());
                userDao.updateUser(user);
            }

            /*
             *If you set a postLoginRequest attribute in the session and forward to about
             *then this will attempt to send the client back to the original page after the login.             
             */
            String forwardStr = (String) request.getSession().getAttribute("postLoginRequest");
            request.getSession().removeAttribute("postLoginRequest");
            if (forwardStr == null) {
                String contextPostLoginRequest = (String) getServletContext().getAttribute("postLoginRequest");
                if (contextPostLoginRequest != null) {
                    forwardStr = (contextPostLoginRequest.indexOf(":") == -1) 
                        ? request.getContextPath() + contextPostLoginRequest 
                        : contextPostLoginRequest;
                }
            }
            if (forwardStr != null) {
                response.sendRedirect(forwardStr);
            } else {
                response.sendRedirect(siteAdminUrl);
                //RequestDispatcher rd = getServletContext().getRequestDispatcher(url);
                //rd.forward(request,response);
            }
        } catch (Throwable t) {
            log.error( t.getMessage() );
            t.printStackTrace();
        }
    }

    public static void sendLoginNotifyEvent(LoginLogoutEvent event, ServletContext context, HttpSession session){
        Object sessionOntModel = null;
        if( session != null )
            sessionOntModel = session.getAttribute("jenaOntModel");
        Object contextOntModel = null;
        if( context != null )
            contextOntModel = context.getAttribute("jenaOntModel");
        
        OntModel jenaOntModel = 
           ( (sessionOntModel != null && sessionOntModel instanceof OntModel) 
            ? (OntModel)sessionOntModel: (OntModel) context.getAttribute("jenaOntModel") );
            
        if( jenaOntModel == null ){
            log.error( "Unable to notify audit model of login event because no model could be found");
        } else {
            if( event == null ){
                log.warn("Unable to notify audit model of login because a null event was passed");
            }else{
                jenaOntModel.getBaseModel().notifyEvent( event );
            }
        }
    }
    
    public static Map<String,HttpSession> getUserURISessionMapFromContext( ServletContext ctx ) {
        Map<String,HttpSession> m = (Map<String,HttpSession>) ctx.getAttribute( USER_SESSION_MAP_ATTR );
        if ( m == null ) {
            m = new HashMap<String,HttpSession>();
            ctx.setAttribute( USER_SESSION_MAP_ATTR, m );
        }
        return m;
    }
     
}

