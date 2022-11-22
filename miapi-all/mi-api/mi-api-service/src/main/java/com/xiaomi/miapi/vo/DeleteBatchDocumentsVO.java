package com.xiaomi.miapi.vo;

import java.util.List;

public class DeleteBatchDocumentsVO {
	
	private String projectID;
	private List<String> documentIDs;
	
	public String getProjectID() {
		return projectID;
	}
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	public List<String> getDocumentIDs() {
		return documentIDs;
	}
	public void setDocumentIDs(List<String> documentIDs) {
		this.documentIDs = documentIDs;
	}
	
	

}
