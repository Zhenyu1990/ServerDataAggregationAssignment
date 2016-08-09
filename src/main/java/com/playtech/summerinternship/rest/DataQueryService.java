package com.playtech.summerinternship.rest;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.playtech.summerinternship.DataForQuery;
import com.playtech.summerinternship.GetDataPoints;
/**
 * /aggr/query
 * Request parameters:
 *  pattern=<string>, mandatory – Regular expression of metric name.
 *  start=<timestamp>, mandatory –
 *  end=<timestamp>, mandatory –
 * Response parameters:
 *  List of metric with parameters:
 * 		name=<string>, mandatory – metric name like 
 * 			local.random.diceroll.1SecondAvg
 * 		datapoints=list of arrays, array consists of two elements:
 * 			First position contains value.
 * 			Second position contains timestamp.
 * @author Zhenyu Wu
 *
 */
@Path("aggr")
public class DataQueryService {

    @Path("/query")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<DataForQuery> DataQuery(@QueryParam("pattern") String pattern, @QueryParam("start") long start, @QueryParam("end") long end) {
    	GetDataPoints getDP = new GetDataPoints(pattern, start, end);
    	ArrayList<DataForQuery> dataList = getDP.findFile();
        return dataList;
    }
	
}
