
package br.com.conductor.heimdall.middleware.spec;

/*-
 * =========================LICENSE_START==================================
 * heimdall-middleware-spec
 * ========================================================================
 * Copyright (C) 2018 Conductor Tecnologia SA
 * ========================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ==========================LICENSE_END===================================
 */

/**
 * This interface provides methods that return a {@link ApiResponse}, a
 * {@link Call}, a {@link DB}, a {@link Http} and a {@link Json}.
 *
 * @author Filipe Germano
 *
 */
public interface Helper {

	/**
	 * Gets a ApiResponse.
	 * 
	 * @return The ApiResponse
	 */
	public ApiResponse apiResponse();

	/**
	 * Gets a Call.
	 * 
	 * @return The Call
	 */
	public Call call();

	/**
	 * Gets a DB.
	 * 
	 * @param databaseName
	 *                         The database name
	 * @return The DB
	 */
	public DB db(String databaseName);

	/**
	 * Gets a DBMongo.
	 * 
	 * @param databaseName
	 *                         The database name
	 * @return The DB
	 */
	public DBMongo dbMongo(String databaseName);

	/**
	 * Gets a Http.
	 * 
	 * @return The Http
	 */
	public Http http();
	
	/**
	 * Define if the Http will use DefaultHandler or HeimdallHandler.
	 * @param useHandler
	 */
	public void httpHandler(boolean useHandler);

	/**
	 * Gets a Json.
	 * 
	 * @return The Json
	 */
	public Json json();

	/**
	 * Gets a Xml.
	 * 
	 * @return The Xml
	 */
	public Xml xml();

}
