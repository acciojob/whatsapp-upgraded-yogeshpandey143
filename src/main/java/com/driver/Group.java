package com.driver;




public class Group {
	
	
    private String name;
    
    private int numberOfParticipants;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfParticipants() {
		return numberOfParticipants;
	}

	public void setNumberOfParticipants(int numberOfParticipants) {
		this.numberOfParticipants = numberOfParticipants;
	}

	public Group(String name, int numberOfParticipants) {
		super();
		this.name = name;
		this.numberOfParticipants = numberOfParticipants;
	}

	public Group() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    

}
