/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.controller.visualization;

/*
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vedit.beans.LoginFormBean;
import edu.cornell.mannlib.vedit.controller.BaseEditController;
import edu.cornell.mannlib.vitro.webapp.beans.Portal;
import edu.cornell.mannlib.vitro.webapp.controller.Controllers;
import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;

/**
 * Services a sparql query.  This will return a simple error message and a 501 if
 * there is no jena Model.
 *
 * @author bdc34
 *
 */
public class DummyVisClientController extends BaseEditController {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(DummyVisClientController.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        this.doGet(request,response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
    	super.doGet(request, response);

    	VitroRequest vreq = handleLoginAuthentication(request, response);
    	prepareVisualizationQueryResponse(request, response, vreq);

        return;
    }

	private void prepareVisualizationQueryResponse(HttpServletRequest request,
			HttpServletResponse response, VitroRequest vreq) {

        Portal portal = vreq.getPortal();

        RequestDispatcher requestDispatcher = request.getRequestDispatcher(Controllers.BASIC_JSP);
        request.setAttribute("bodyJsp", "/templates/visualization/dummy_vis_client.jsp");
        request.setAttribute("portalBean", portal);
        request.setAttribute("title", "Dummy Visualization Client");
        request.setAttribute("scripts", "/templates/visualization/visualization_scripts.jsp");

		try {
			requestDispatcher.forward(request, response);
		} catch (Exception e) {
			log.error("EntityEditController could not forward to view.");
			log.error(e.getMessage());
			log.error(e.getStackTrace());
		}

	}

	private VitroRequest handleLoginAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		//        This might not be required
		        /*
		         * why are there multiple places where the login is checked? shud be abtracted into
		         * new methoid?
		         * */
//		        if( !checkLoginStatus(request, response) )
//		        	return null;

		        VitroRequest vreq = new VitroRequest(request);

		        Object obj = vreq.getSession().getAttribute("loginHandler");
		        LoginFormBean loginHandler = null;

		        if( obj != null && obj instanceof LoginFormBean )
		            loginHandler = ((LoginFormBean)obj);


		        /*
		         * what is the speciality of 5 in the conditions?
		         *
		        if( loginHandler == null ||
		            ! "authenticated".equalsIgnoreCase(loginHandler.getLoginStatus()) ||
		             Integer.parseInt(loginHandler.getLoginRole()) <= 5 ){
		            HttpSession session = request.getSession(true);

		            session.setAttribute("postLoginRequest",
		                    vreq.getRequestURI()+( vreq.getQueryString()!=null?('?' + vreq.getQueryString()):"" ));
		            String redirectURL = request.getContextPath() + Controllers.SITE_ADMIN + "?login=block";
		            response.sendRedirect(redirectURL);
		            return null;
		        }*/
		return vreq;
	}

}
