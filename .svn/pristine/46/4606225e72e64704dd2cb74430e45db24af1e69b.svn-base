package com.wcs.common.filenet.pe;

import java.io.InputStream;
import org.apache.log4j.Logger;
import filenet.vw.api.VWException;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWTransferResult;
import filenet.vw.api.VWWorkflowDefinition;

/**
 * Helper class for Workflow. Provides methods to manipulate workflow in process
 * engine runtime.
 */
public class WorkflowHelper {
	private static Logger log = Logger.getLogger(WorkflowHelper.class);

	/**
	 * Tests whether a workflow defintion with the given name has been
	 * transfered to the Process Engine runtime.
	 * 
	 * @param wfDefName
	 *            The workflow definition name.
	 * @param vwSession
	 *            The <tt>VWSession</tt>.
	 * @return <tt>true</tt> If a workflow exists on the Process Engine runtime;
	 *         <tt>false</tt> otherwise.
	 * @throws VWException
	 */
	public static boolean isTransfered(String wfDefName, VWSession vwSession)
			throws VWException {
		String[] workClassNames = vwSession.fetchWorkClassNames(false);
		for (int i = 0; i < workClassNames.length; i++) {
			if (wfDefName.equals(workClassNames[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Launches a workflow defined by a transferred workflow definition, as
	 * specified by the workflow version or the workflow definition name (work
	 * class name). If the workflowIdentifier is not valid, a local workflow
	 * will be transfered.
	 * </p>
	 * 
	 * @param workflowIdentifier
	 *            The workflow definition name (work class name) as returned by
	 *            VWWorkflowDefinition.getName() method, or the workflow version
	 *            (version property) as returned by
	 *            VWTransferResult.getVersion().
	 * @param wfDefFile
	 *            the local workflow defination file.
	 * @return A VWStepElement object that represents the launch step for the
	 *         workflow.
	 * @throws VWException
	 *             if fails to create the workflow.
	 */
	public VWStepElement createWorkflow(String workflowIdentifier,
			String wfDefFile, VWSession vwSession) throws VWException {
		if (log.isDebugEnabled()) {
			log.debug("createWorkflow()-- workflowIdentifier = "
					+ workflowIdentifier);
		}
		VWStepElement launchStep = null;
		if (!checkWorkflowIdentifier(workflowIdentifier, vwSession)) {
			// if the workflowIdentifier is invalid, transfer a new workflow
			log.warn("The workflowIdentifier is invalid.( id="
					+ workflowIdentifier + " )");
			log.warn("A default workflow definition will be transfered.");
			VWWorkflowDefinition wflDef = null;
			wflDef = openDefinition(wfDefFile);
			if (log.isDebugEnabled()) {
				log.debug("workflow definition name (work class name):"
						+ wflDef.getName());
			}
			workflowIdentifier = transferDefinition(wflDef, vwSession);
			log.warn("New workflowIdentifier(version): " + workflowIdentifier);
		}
		launchStep = vwSession.createWorkflow(workflowIdentifier);
		return launchStep;
	}

	/**
	 * Indicates whether or not a workflow is on the Process Engine runtime
	 * (isolated region), based on the specified workflow version ID or workflow
	 * definition name.
	 * 
	 * @param workflowIdentifier
	 *            workflow version ID or workflow definition name.
	 * @return A boolean value of true if a workflow with the ID specified in
	 *         the workflowIdentifier parameter exists on the Process Engine;
	 *         false otherwise.
	 * @throws VWException
	 *             if an error occurs.
	 */
	private boolean checkWorkflowIdentifier(String workflowIdentifier,
			VWSession vwSession) throws VWException {
		// first check if workflowIdentifier is a valid version ID.
		if (vwSession.checkWorkflowIdentifier(workflowIdentifier)) {
			return true;
		}
		// then check if workflowIdentifier is a valid work class name.
		boolean nameExist = false;
		String[] workClassNames = vwSession.fetchWorkClassNames(false);
		// check if the workflowIdentifier is a valid work class name;
		for (int i = 0; i < workClassNames.length; i++) {
			if (workflowIdentifier.equals(workClassNames[i])) {
				nameExist = true;
				break;
			}
		}
		return nameExist;
	}

	/**
	 * Creates a workflow definition object from a file
	 * 
	 * @param workflowDefFile
	 *            a string containing the workflow definition file path
	 * @return a workflow definition object
	 * @throws VWException
	 *             if an error occurs.
	 */
	private VWWorkflowDefinition openDefinition(String workflowDefFile)
			throws VWException {
		VWWorkflowDefinition wflDef = null;
		if (log.isDebugEnabled()) {
			log.debug("Read workflow file: " + workflowDefFile);
		}
		InputStream in = WorkflowHelper.class
				.getResourceAsStream(workflowDefFile);
		wflDef = VWWorkflowDefinition.read(in);
		return wflDef;
	}

	/**
	 * Transfers the workflow definition information
	 * 
	 * @param wflDef
	 *            a VWWorkflowDefinition object.
	 * @return if successful, a string containing the vwversion.
	 * @throws VWException
	 *             if an error occurs.
	 */
	public static String transferDefinition(VWWorkflowDefinition wflDef,
			VWSession vwSession) throws VWException {
		VWTransferResult transferResult = null;
		String vwVersion = null;
		// Transfer the workflow definition. The second parameter is a unique id
		// which will be used to indentify this workflow. We use the
		// lib/docid/version
		// from content services for this value.
		transferResult = vwSession.transfer(wflDef, null, true, false);
		if (transferResult.success()) {
			if (log.isDebugEnabled()) {
				log.debug("The transfer was successful.");
			}
			// retrieve the version information
			vwVersion = transferResult.getVersion();
		} else {
			// display the transfer errors
			log.error("The following transfer errors occured:");
			String[] errors = transferResult.getErrors();
			for (int i = 0; i < errors.length; i++) {
				log.error(errors[i]);
			}
		}
		return vwVersion;
	}

	/**
	 * @param workFlowName
	 *            流程图的名称
	 * @return VWStepElement 对象
	 * @throws VWException
	 */
	public static VWStepElement createWorkflow(String workFlowName,
			VWSession vwSession) throws VWException {
		VWStepElement launchStep = null;
		launchStep = vwSession.createWorkflow(workFlowName);
		return launchStep;
	}

}
