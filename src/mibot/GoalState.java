/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package mibot;

import soc.qase.state.Origin;

/**
 *
 * @author alvarin
 * Clase que almacena el estado del bot al alcanzar un objetivo
 */
public class GoalState {
    
    int health;
    double healthDistance;
    int armor;
    double armorDistance;
    int ammo;
    double ammoDistance;
    int haveHyper;
    double hyperDistance;
    Origin position;
    
    public GoalState(int h,int ar,int am,int hy,Origin pos,Origin hD,Origin arD,Origin amD,Origin hyD)
    {
        health = h;      
        healthDistance = Math.sqrt((pos.getX()-hD.getX())^2 + (pos.getY()-hD.getY())^2 + (pos.getZ()-hD.getZ())^2);
        armor = ar;
        armorDistance = Math.sqrt((pos.getX()-arD.getX())^2 + (pos.getY()-arD.getY())^2 + (pos.getZ()-arD.getZ())^2);
        ammo = am;
        ammoDistance = Math.sqrt((pos.getX()-amD.getX())^2 + (pos.getY()-amD.getY())^2 + (pos.getZ()-amD.getZ())^2);
        haveHyper = hy;
        hyperDistance = Math.sqrt((pos.getX()-hyD.getX())^2 + (pos.getY()-hyD.getY())^2 + (pos.getZ()-hyD.getZ())^2);     
        position = pos;
    }
    
}
