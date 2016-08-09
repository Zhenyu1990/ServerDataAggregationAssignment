package com.playtech.summerinternship;

import java.util.ArrayList;

/**
 * This class is used to store file information and will be return by servlet
 * and be used to generate json string
 * 
 * @author Zhenyu Wu
 *
 */
public class DataForQuery {

	private String name;
	private ArrayList<long[]> datapoints;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<long[]> getDatapoints() {
		return datapoints;
	}
	
	public void setDatapoints(ArrayList<long[]> datapoints) {
		this.datapoints = datapoints;
	}

	public DataForQuery() {}
	
	public DataForQuery(String name, ArrayList<long[]> datapoints) {
		super();
		this.name = name;
		this.datapoints = datapoints;
	}

	@Override
	public String toString() {
		return "DataForQuery [name=" + name + ", datapoints=" + datapoints + "]";
	}
	
}
