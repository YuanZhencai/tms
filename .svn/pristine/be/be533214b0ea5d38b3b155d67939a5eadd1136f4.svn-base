//  ------------------------------------------------------------
//  $Revision:   1.7  $
//  $Date:   09 May 2007 15:48:48  $
//  $Workfile:   RosterHelper.java  $
//  ------------------------------------------------------------
//  All Rights Reserved.  Copyright (c) 1998-2007 FileNet Corp.
//  ------------------------------------------------------------

package com.wcs.common.filenet.pe;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import filenet.vw.api.*;

/**
 * This demonstration sample class illustrates how to make use of the VWSession,
 * VWRoster, VWRosterQuery, VWRosterElement, and VWWorkObject objects.
 * 
 * @since eProcess 4.1
 * @see Logger
 */
public class RosterHelper extends Object {
	// declare variables
	private VWSession m_vwSession = null;
	private static Logger m_logger = null;
	private static Log log = LogFactory.getLog(RosterHelper.class);

	/**
	 * Constructor - performs initialization
	 * 
	 * @param vwSession
	 *            a VWSession object.
	 * @param logger
	 *            a Logger object.
	 * @since eProcess 4.1
	 */
	public RosterHelper(VWSession vwSession, Logger logger) {
		m_vwSession = vwSession;
		m_logger = logger;
	}

	/**
	 * Displays the process information from the items in the "DefaultRoster"
	 * 
	 * @since eProcess 4.1
	 */
	public void displayProcessInformation() {
		VWRoster vwRoster = null;
		VWRosterQuery rQuery = null;
		VWRosterElement rosterElement = null;
		VWProcess process = null;
		VWWorkflowHistory wflHistory = null;
		VWStepHistory stepHistory = null;
		VWWorkObject workObject = null;

		try {
			if (m_vwSession == null) {
				m_logger.log("Invalid session: <null> (displayProcessInformation)");
				return;
			}

			// get the roster object for the DefaultRoster.
			vwRoster = m_vwSession.getRoster("DefaultRoster");
			m_logger.log("Displaying process information for roster:  "
					+ vwRoster.toString());

			// Set the maximum number of items to be retrieved in each server
			// fetch transaction.
			// In this case, setting the value to 25 will require less memory
			// for each fetch
			// than the default setting (50).
			vwRoster.setBufferSize(25);

			// construct a roster query object and query for all elements.
			rQuery = vwRoster.createQuery(null, null, null, 0, null, null,
					VWFetchType.FETCH_TYPE_ROSTER_ELEMENT);
			if (rQuery.hasNext()) {
				do {
					// get a roster element
					rosterElement = (VWRosterElement) rQuery.next();
					m_logger.log("\nVWProcess information for roster element:\n");

					// get process from roster element
					workObject = rosterElement.fetchWorkObject(false, false);
					if (workObject != null) {
						process = workObject.fetchProcess();
					}
					if (process == null) {
						continue;
					}

					// get workflow history & log key information
					// N.B. The mapId parameter is currently always -3 (PW 4.0).
					// For future reference, this value is contained in field
					// F_InstrId.
					wflHistory = process.fetchWorkflowHistory(-3);

					// get the launch date of the workflow
					m_logger.log("\tWorkflow Launch Date: "
							+ wflHistory.getLaunchDate());

					// iterate through the step history elements
					m_logger.log("\n\tStep Histories:\n");

					if (wflHistory.hasNext()) {
						do {
							stepHistory = wflHistory.next();
							m_logger.log("\t\tStepId:  "
									+ stepHistory.getStepId());
						} while (wflHistory.hasNext());
					} else {
						m_logger.log("\t\tNo Step Histories exist.");
					}

					// now iterate through the VWWorkObjects
					m_logger.log("\n\tWork Objects:\n");

					if (process.hasNext()) {
						do {
							workObject = process.next();
							m_logger.log("\t\tWork Object Id: "
									+ workObject.getWorkObjectNumber());

							// get lock state
							if (workObject.fetchLockedStatus() == 0) {
								m_logger.log("\t\t\tObject is not locked.");
							}else {
								m_logger.log("\t\t\tObject is locked.");
							}
						} while (process.hasNext());
					} else {
						m_logger.log("\t\tNo Step Histories exist.");
					}
				} while (rQuery.hasNext());
			} else {
				m_logger.log("\t No roster elements, therefore can't get VWProcess information.");
			}
		} catch (Exception ex) {
			if (m_logger != null) {
				m_logger.log(ex);
			} else {
				log.error("displayProcessInformation方法出现异常", ex);
			}
		}
	}

	/**
	 * Displays the contents of the "DefaultRoster"
	 * 
	 * @since eProcess 4.1
	 */
	public void displayRosterContents() {
		VWRoster vwRoster = null;

		try {
			if (m_vwSession != null) {
				// get the roster object for the DefaultRoster.
				vwRoster = m_vwSession.getRoster("DefaultRoster");
				m_logger.log("Retrieving information for roster:  "
						+ vwRoster.toString() + "\n");

				// Get the roster depth
				m_logger.log("Roster element count:  " + vwRoster.fetchCount());

				// display roster elements
				displayRosterElements(vwRoster);

				// display roster work objects
				displayWorkObjects(vwRoster);
			}
		} catch (Exception ex) {
			if (m_logger != null) {
				m_logger.log(ex);
			} else {
				log.error("displayRosterContents方法出现异常", ex);
			}
		}
	}

	/**
	 * Queries for and displays the VWRosterElements in the specified VWRoster
	 * object
	 * 
	 * @param vwRoster
	 *            a VWRoster object
	 * @since eProcess 4.1
	 */
	public void displayRosterElements(VWRoster vwRoster) {
		VWRosterQuery rElemQuery = null;
		VWRosterElement rosterElement = null;

		try {
			// do we have a valid VWQueue object?
			if (vwRoster == null) {
				m_logger.log("The roster object is null!");
				return;
			}

			// construct a roster query object and query for all elements.
			rElemQuery = vwRoster.createQuery(null, null, null, 0, null, null,
					VWFetchType.FETCH_TYPE_ROSTER_ELEMENT);

			// fetch the first roster element using the VWRosterQuery object
			rosterElement = (VWRosterElement) rElemQuery.next();

			// check to see if there are any roster elements
			if (rosterElement == null) {
				m_logger.log("\t Roster elements: none");
			} else {
				String[] fieldNames = null;
				Object value = null;

				// iterate through the roster elements
				do {
					m_logger.log("\t Roster element:");

					// display System Fields
					// Note: There are no User defined fields on a
					// roster element at this time (VW 3.0)
					fieldNames = rosterElement.getFieldNames();
					if (fieldNames == null) {
						m_logger.log("\t\t no Fields!");
					} else {
						m_logger.log("\t\t Fields:");

						// iterate through each field
						for (int i = 0; i < fieldNames.length; i++) {
							if (fieldNames[i] != null) {
								value = rosterElement
										.getFieldValue(fieldNames[i]);

								// Display the field names and their values
								m_logger.log("\t\t\t" + fieldNames[i] + "="
										+ value);
							}
						}
					}

					// print Specialized Data
					m_logger.log("\n\t\tOther Information:");
					String bvalue = rosterElement.getWorkObjectNumber();
					m_logger.log("\t\t\t" + "WorkObjectNumber" + "=" + bvalue);

					String svalue = rosterElement.getWorkObjectName();
					m_logger.log("\t\t\t" + "WorkObjectName" + "=" + svalue);

					svalue = rosterElement.getTag();
					m_logger.log("\t\t\t" + "Tag" + "=" + svalue);

					int ivalue = rosterElement.getServerLocation();
					m_logger.log("\t\t\t" + "CurrentServerLocation" + "="
							+ ivalue);
				} while ((rosterElement = (VWRosterElement) rElemQuery.next()) != null);
			}
		} catch (Exception ex) {
			if (m_logger != null) {
				m_logger.log(ex);
			} else {
				log.error("displayRosterElements方法出现异常", ex);
			}
		}
	}

	/**
	 * Queries for and displays the VWWorkObjects in the specified VWRoster
	 * object
	 * 
	 * @param vwRoster
	 *            a VWRoster object
	 * @since eProcess 4.1
	 */
	public void displayWorkObjects(VWRoster vwRoster) {
		VWRosterQuery rWobQuery = null;
		VWWorkObject vwWorkObject = null;

		try {
			// do we have a valid VWQueue object?
			if (vwRoster == null) {
				m_logger.log("The roster object is null!");
				return;
			}

			// construct a roster query object and query for all elements.
			rWobQuery = vwRoster.createQuery(null, null, null, 0, null, null,
					VWFetchType.FETCH_TYPE_WORKOBJECT);

			// fetch the first roster element using the VWRosterQuery object
			vwWorkObject = (VWWorkObject) rWobQuery.next();

			// check to see if there are any roster elements
			if (vwWorkObject == null) {
				m_logger.log("\t Roster work objects: none");
			} else {
				// iterate through the work objects
				do {
					// display the work object information
					m_logger.displayWorkObjectInfo(vwWorkObject);
				} while ((vwWorkObject = (VWWorkObject) rWobQuery.next()) != null);
			}
		} catch (Exception ex) {
			if (m_logger != null) {
				m_logger.log(ex);
			} else {
				log.error("displayWorkObjects方法出现异常", ex);
			}
		}
	}

	/**
	 * TMS
	 */

	/**
	 * Queries for and displays the VWWorkObjects in the specified VWRoster
	 * object
	 * 
	 * @param vwRoster
	 *            a VWRoster object
	 * @since eProcess 4.1
	 */
	public static List<VWWorkObject> getWorkObjects(VWSession vwSession) {
		List<VWWorkObject> wobs = new ArrayList<VWWorkObject>();
		VWRosterQuery rWobQuery = null;

		try {
			VWRoster vwRoster = vwSession.getRoster("DefaultRoster");
			// do we have a valid VWQueue object?
			if (vwRoster == null) {
				m_logger.log("The roster object is null!");
				return wobs;
			}
			// construct a roster query object and query for all elements.
			rWobQuery = vwRoster.createQuery(null, null, null, 0, null, null,
					VWFetchType.FETCH_TYPE_WORKOBJECT);
			// fetch the first roster element using the VWRosterQuery object
			// check to see if there are any roster elements
			if (rWobQuery == null) {
				m_logger.log("\t Roster work objects: none");
			} else {
				while (rWobQuery.hasNext()) {
					VWWorkObject vwWorkObject = (VWWorkObject) rWobQuery.next();
					wobs.add(vwWorkObject);
				}
			}

		} catch (Exception ex) {
			if (m_logger != null) {
				m_logger.log(ex);
			} else {
				log.error("getWorkObjects方法出现异常", ex);
			}
		}
		return wobs;
	}

	/**
	 * 得到流程实例by filter
	 * 
	 * @param vwSession
	 * @param filter
	 * @param substitutionVars
	 * @return
	 */
	public static List<VWWorkObject> getWorkObjects(VWSession vwSession,
			String filter, Object[] substitutionVars) {
		List<VWWorkObject> wobs = new ArrayList<VWWorkObject>();
		VWRosterQuery rWobQuery = null;
		try {
			VWRoster vwRoster = vwSession.getRoster("DefaultRoster");
			// do we have a valid VWQueue object?
			if (vwRoster == null) {
				m_logger.log("The roster object is null!");
				return wobs;
			}

			rWobQuery = vwRoster.createQuery(null, null, null, 0, filter,
					substitutionVars, VWFetchType.FETCH_TYPE_WORKOBJECT);
			if (rWobQuery == null) {
				m_logger.log("\t Roster work objects: none");
			} else {
				while (rWobQuery.hasNext()) {
					VWWorkObject vwWorkObject = (VWWorkObject) rWobQuery.next();
					wobs.add(vwWorkObject);
				}
			}
		} catch (Exception ex) {
			if (m_logger != null) {
				m_logger.log(ex);
			} else {
				log.error("getWorkObjects方法 得到流程实例by filter出现异常", ex);
			}
		}
		return wobs;
	}

	/**
	 * 
	 * <p>
	 * Description:根据流程实例编号查询具体工作项
	 * </p>
	 * 
	 * @param vwSession
	 * @param wokrObjectNumber
	 * @param queryFlags
	 * @param fetchType
	 * @return
	 * @throws VWException
	 * @throws Exception
	 */
	public static VWWorkObject vwfetchWorkObject(VWSession vwSession,
			String wokrObjectNumber, int queryFlags) throws VWException {
		VWRosterQuery rWobQuery = null;
		VWRoster vwRoster;
		try {
			vwRoster = vwSession.getRoster("DefaultRoster");
			if (vwRoster == null) {
				return null;
			}
			String filter = "F_WobNum = :WobNum";
			Object[] substitutionVars = { new VWWorkObjectNumber(
					wokrObjectNumber) };
			rWobQuery = vwRoster
					.createQuery(null, null, null, queryFlags, filter,
							substitutionVars, VWFetchType.FETCH_TYPE_WORKOBJECT);
			if (rWobQuery.hasNext()) {
				VWWorkObject vwWorkObject = (VWWorkObject) rWobQuery.next();
				if (rWobQuery.hasNext()) {
					throw new IllegalStateException("该流程下产生多个工作对象，流程实例编号为"
							+ wokrObjectNumber);
				}
				return vwWorkObject;
			} else {
				throw new NullPointerException("没有工作对象");
			}
		} catch (VWException e) {
			log.error("vwfetchWorkObject方法 根据流程实例编号查询具体工作项出现异常", e);
			throw e;
		}

	}
	// --------------------------------------
	// private methods
	// --------------------------------------
}
