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

import java.util.*;

import net.tinyos.tinysoa.common.*;

/*******************************************************************************
 * Interface that defines the information service.
 * 
 * @author		Edgardo Avilés López
 * @version	0.1, 07/24/2006
 ******************************************************************************/
public interface InfoServ {
	
	/***************************************************************************
	 * Returns a listing of the available network services.
	 * 
	 * @return A vector with the available network services
	 **************************************************************************/
	public Vector<Network> getNetworksList();
	
}
