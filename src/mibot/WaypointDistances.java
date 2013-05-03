/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package mibot;


import soc.qase.state.Origin;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.ai.waypoint.Waypoint;
/**
 *
 * @author alvarin
 */
public class WaypointDistances 
{
    Origin origin;
    Origin[] restOfOrigins;
    double[] distances;
    
    WaypointDistances()
    {
        
    }
    
    WaypointDistances(Origin or,Origin[] rest,int restLength,WaypointMap map,boolean actualPos)
    {
        int count = 0;
        origin = new Origin(or);
        if(actualPos)
        {
            restOfOrigins = new Origin[restLength];
            distances = new double[restLength];
        }
        else
        {
            restOfOrigins = new Origin[restLength-1];
            distances = new double[restLength-1];
        }
        for(int i=0;i<restLength;i++)
        {
            if(!origin.equals(rest[i])) 
            {
                restOfOrigins[count] = new Origin(rest[i]);
                distances[count] = 0;
                count++;
            }
        }
        Waypoint[] waypoints;
        for(int i=0;i<restOfOrigins.length;i++)
        {
            waypoints = map.findShortestPath(origin,restOfOrigins[i]);
            for(int j=0;j<waypoints.length;j++)
            {
                if(j < waypoints.length - 1)
                {
                    distances[i] = distances[i] + euclideanDistance(waypoints[j].getPosition().toOrigin(),waypoints[j+1].getPosition().toOrigin());
                }
            }
        }     
    }
    
    
    public double getWaypointDistanceToOrigin(Origin or)
    {
        for(int i=0;i<restOfOrigins.length;i++)
        {
            if(or.equals(restOfOrigins[i])) return distances[i];
        }
        return 0;
    }
    
    private double euclideanDistance(Origin o1,Origin o2)
    {
        return Math.sqrt((o1.getX()-o2.getX())*(o1.getX()-o2.getX())+(o1.getY()-o2.getY())*(o1.getY()-o2.getY())+(o1.getZ()-o2.getZ())*(o1.getZ()-o2.getZ()));
    }
    
}
