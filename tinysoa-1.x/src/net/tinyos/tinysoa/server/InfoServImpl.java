/*
 *  Copyright 2007 Edgardo Avilés López
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 ******************************************************************************/

package net.tinyos.tinysoa.server;

import java.sql.*;
import java.util.*;
import java.net.*;
import javax.jws.*;

import net.tinyos.tinysoa.common.*;
import net.tinyos.tinysoa.util.*;

/*******************************************************************************
 * Class that implements the information service functionality.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
@WebService(name="InfoServ", targetNamespace="http://numenor.cicese.mx/TinySOA")
public class InfoServImpl implements InfoServ {
	
	private Connection db;
	private String port;
	
	/***************************************************************************
	 * Class constructor.
	 * 
	 * @param db	Database connector object to use
	 * @param port	Port from which the services are provided
	 **************************************************************************/
	public InfoServImpl(Connection db, String port) {
		this.db = db;
		this.port = port;
	}
	
	/***************************************************************************
	 * Returns a listing containing the services provided by the server.
	 * 
	 * @return	A vector with the services information
	 * @see		Network
	 **************************************************************************/
	@WebMethod(operationName="getNetworksList", action="urn:getNetworksList")
	@WebResult(name="networksListResult")
	public Vector<Network> getNetworksList() {
		Vector<Network> networks = new Vector<Network>();
		
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = db.createStatement();
			rs = st.executeQuery("SELECT * FROM networks ORDER BY name");
			
			while (rs.next()) {
				Network r = new Network();
				r.setId(rs.getInt("id"));
				r.setName(rs.getString("name"));
				r.setDescription(rs.getString("description"));
				r.setWsdl("http://" + InetAddress.getLocalHost().getHostAddress() +
						":" + port + "/NetServ" + r.getId() + "?wsdl");
				networks.add(r);
			}
			
		} catch (SQLException ex) {
			Errors.errorBD(ex);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if ((rs != null) && (st != null)) {
				try {
					rs.close();
					st.close();
				} catch (Exception e) {}
			}
		}
		
		return networks;
	}
	
}
