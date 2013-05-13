package mibot;

import java.io.IOException;
import java.util.Vector;
import java.util.Random;

//import soc.qase.*;

import soc.qase.bot.ObserverBot;
import soc.qase.file.bsp.BSPParser;
import soc.qase.tools.vecmath.Vector3f;


import soc.qase.state.*;

import java.lang.Math;
import java.util.logging.Level;
import java.util.logging.Logger;
import jess.*;
import soc.qase.ai.waypoint.Waypoint;
import soc.qase.ai.waypoint.WaypointMap;
import soc.qase.ai.waypoint.WaypointMapGenerator;
import soc.qase.com.*;


//Cualquier bot debe extender a la clase ObserverBot, para hacer uso de sus funcionalidades
public final class MiBotseMueve extends ObserverBot
{
	//Variables 
	private World world = null;
	private Player player = null;
    private Proxy proxy = new Proxy();
	
        private Rutas rutas = new Rutas();
        
        private Vector3f PosPlayer= new Vector3f(0, 0, 0);
	
	//Variable que almacena la posiciÃ³n previa del jugador en 3D, inicializada en 0,0,0
	private Vector3f prevPosPlayer = new Vector3f(0, 0, 0);
	
	//Variable que nos permiten ajustar la lÃ³gica y velocidad del movimiento del bot
	private int nsinavanzar = 0, velx = 50 ,vely = 50, cambios = 0;
	
	//Acceso a la informaciÃ³n del entorno
	private BSPParser mibsp = null;
	
	// Distancia al enemigo que estamos atacando
	private float distanciaEnemigo = Float.MAX_VALUE;
	
	final int BLASTER = 7, SHOTGUN = 8, SUPER_SHOTGUN = 9,
			MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12, GRENADE_LAUNCHER = 13,
			ROCKET_LAUNCHER = 14, HYPERBLASTER = 15, RAILGUN = 16, BFG10K = 17,
			SHELLS = 18, BULLETS = 19, CELLS = 20, ROCKETS = 21, SLUGS = 22;
	//Para consultar se accede con (arma - 7);
	final int[] WEAPON_CDS = { 4, 11, 11, 0, 0, 13, 11, 8, 0, 15, 24 };
	//Hay que tener en cuenta que la escopeta lanza 12 balas
	final int[] WEAPON_DAMAGE = {15, 16, 24, 8, 6, 0, 120, 100, 15, 100, 200};
	//Weapon accuracy
	int[] WEAPON_ACCURACY = {1,1,1,1,1,1,1,1,1,1,1};
	//en Filas -> arma-7
	//en Columnas 0 -> �ndice de tipo de munici�n
	//en Columnas 1 -> munici�n por recogida
	//en Columnas 2 -> munici�n m�xima
	
	int [][] weaponsIndex = new int[11][3];
	
	// Motor de inferencia
	private Rete engine;
        //Spawn, SeekItem, Battle_Chase, Battle_Retreat, Battle_Engage
        private String State = "Spawn";
        
        private Waypoint[] route;
        
        private int healthLowLimit = 40;

        private int healthHighLimit = 80;
        private int armorLowLimit = 30;
        private int armorHighLimit = 80;
               
        
        int dire = 0;
        
        Origin targetPos;

        
        static int [] allyStates = {0,0,0,0};
        static Origin [] allyPositions = new Origin[4];
        
        
/*-------------------------------------------------------------------*/
/**	Constructor que permite especificar el nombre y aspecto del bot
 *	@param botName Nombre del bot durante el juego
 *	@param botSkin Aspecto del bot */
/*-------------------------------------------------------------------*/
	public MiBotseMueve(String botName, String botSkin)
	{
		super((botName == null ? "MiBotseMueve" : botName), botSkin);
		initBot();
	}

/*-------------------------------------------------------------------*/
/**	Constructor que permite ade mÃ¡s de especificar el nombre y aspecto 
 *	del bot, indicar si Ã©ste analizarÃ¡ manualmente su inventario.
 *	@param botName Nombre del bot durante el juego
 *	@param botSkin Aspecto del bot
 *	@param trackInv Si true, El agente analizarÃ¡ manualmente su inventario */
/*-------------------------------------------------------------------*/
	public MiBotseMueve(String botName, String botSkin, boolean trackInv)
	{
		super((botName == null ? "MiBotseMueve" : botName), botSkin, trackInv);
		initBot();
	}

/*-------------------------------------------------------------------*/
/**	Constructor que permite ademÃ¡s de especificar el nombre y aspecto 
 *	del bot, indicar si Ã©ste analizarÃ¡ manualmente su inventario y
 *  si harÃ¡ uso de un hilo en modo seguro.
 *	@param botName Nombre del bot durante el juego
 *	@param botSkin Aspecto del bot
 *	@param highThreadSafety Si true, permite el modo de hilo seguro
 *	@param trackInv Si true, El agente analizarÃ¡ manualmente su inventario */
/*-------------------------------------------------------------------*/
	public MiBotseMueve(String botName, String botSkin, boolean highThreadSafety, boolean trackInv)
	{
		super((botName == null ? "MiBotseMueve" : botName), botSkin, highThreadSafety, trackInv);
		initBot();
	}

/*-------------------------------------------------------------------*/
/**	Constructor que permite ademÃ¡s de especificar el nombre, aspecto 
 *	del bot y la clave del servidor, indicar si Ã©ste analizarÃ¡ manualmente 
 *  su inventario y si harÃ¡ uso de un hilo en modo seguro.
 *	@param botName Nombre del bot durante el juego
 *	@param botSkin Aspecto del bot
 *	@param password clave del servidor
 *	@param highThreadSafety Si true, permite el modo de hilo seguro
 *	@param trackInv Si true, El agente analizarÃ¡ manualmente su inventario */
/*-------------------------------------------------------------------*/
	public MiBotseMueve(String botName, String botSkin, String password, boolean highThreadSafety, boolean trackInv)
	{
		super((botName == null ? "MiBotseMueve" : botName), botSkin, password, highThreadSafety, trackInv);
		initBot();
	}

/*-------------------------------------------------------------------*/
/**	Constructor que permite ademÃ¡s de especificar el nombre, aspecto 
 *	del bot, ratio de comunicaciÃ³n, tipo de mensajes y la clave del servidor,
 *  indicar si Ã©ste analizarÃ¡ manualmente 
 *  su inventario y si harÃ¡ uso de un hilo en modo seguro.
 *  @param botName Nombre del bot durante el juego
 *	@param botSkin Aspecto del bot
 *	@param recvRate Ratio de comunicaciÃ³n 
 *	@param msgLevel Tipo de mensajes
 *	@param fov Campo de visiÃ³n del agente
 *	@param hand Indica la mano en la que se lleva el arma
 *	@param password Clave del servidor
 *	@param highThreadSafety Si true, permite el modo de hilo seguro
 *	@param trackInv Si true, El agente analizarÃ¡ manualmente su inventario */
/*-------------------------------------------------------------------*/
	public MiBotseMueve(String botName, String botSkin, int recvRate, int msgLevel, int fov, int hand, String password, boolean highThreadSafety, boolean trackInv)
	{
		super((botName == null ? "MiBotseMueve" : botName), botSkin, recvRate, msgLevel, fov, hand, password, highThreadSafety, trackInv);
		initBot();
	}

	//InicializaciÃ³n del bot
	private void initBot()
	{		
		this.setCTFTeam(1);
		//Autorefresco del inventario
		this.setAutoInventoryRefresh(true);

		// 0 = associated ammo inventory index, 1 = ammo per pickup, 2 = max ammo
		//Adem�s los indices de 0/10 son armas
		weaponsIndex[0][0] = -1;	weaponsIndex[0][1] = -1;	weaponsIndex[0][2] = -1;
		weaponsIndex[1][0] = 18;	weaponsIndex[1][1] = 10;	weaponsIndex[1][2] = 100;
		weaponsIndex[2][0] = 18;	weaponsIndex[2][1] = 10;	weaponsIndex[2][2] = 100;
		weaponsIndex[3][0] = 19;	weaponsIndex[3][1] = 50;	weaponsIndex[3][2] = 200;
		weaponsIndex[4][0] = 19;	weaponsIndex[4][1] = 50;	weaponsIndex[4][2] = 200;
		weaponsIndex[5][0] = 12;	weaponsIndex[5][1] = 5;		weaponsIndex[5][2] = 50;
		weaponsIndex[6][0] = 12;	weaponsIndex[6][1] = 5;		weaponsIndex[6][2] = 50;
		weaponsIndex[7][0] = 21;	weaponsIndex[7][1] = 5;		weaponsIndex[7][2] = 50;
		weaponsIndex[8][0] = 20;	weaponsIndex[8][1] = 50;	weaponsIndex[8][2] = 200;
		weaponsIndex[9][0] = 22;	weaponsIndex[9][1] = 10;	weaponsIndex[9][2] = 50;
		weaponsIndex[10][0] = 20;	weaponsIndex[10][1] = 50;	weaponsIndex[10][2] = 200;

	}

        //Distintos valores que puede dar decideBattle()
        int FIGHT = 1;
        int CHASE = 2;
        int RUNAWAY = 3;
        
        //Posicion del objetivo a largo plazo
        Origin goalPos;
        
        //El objetivo se ha cumplido o no;
        boolean goal = false;
        
        //Posicion del siguiente waypoint al que ir
        Origin nextWayPoint;
        
        int hasRoute = 0;
        int routeLength;
        int actualWayPoint = 0;
        int arrived = 0;
        Origin enemyPos = new Origin(0,0,0);
/*-------------------------------------------------------------------*/
/**	Rutina central del agente para especificar su comportamiento
 *	@param w Objeto de tipo World que contiene el estado actual del juego */
/*-------------------------------------------------------------------*/
        
        
        public void setMap(WaypointMap map)
        {
            this.wpMap = map;
        }
        
        Entity enemy;
        
        int count = 0;

        boolean shortTermGoal = false;
        Origin[] longTermGoalPath = null;
        boolean seen = false;
        int countGoalPath = 0;
        int lengthGoalPath;
        boolean enemyDead = false;        
        Origin antPos = new Origin();
        
        public void runAI(World w)
	{

            
            if (mibsp==null) mibsp = new BSPParser(rutas.BSP_path);

            
            world = w;
            player = world.getPlayer();
            
            if(this.euclideanDistance(antPos, player.getPosition()) > 50)
            {
                countGoalPath = 0;
                goal = false;
            }
            
            antPos.setX(player.getPosition().getX());
            antPos.setY(player.getPosition().getY());
            antPos.setZ(player.getPosition().getZ());
            

            Vector opponents = world.getOpponents(true);

			
                /* if(hasRoute==0)
                {
                    hasRoute = 1; 
                    route = findShortestPathToWeapon(null);
                    routeLength = route.length;


                }*/


           /* targetPos = new Origin();
 

            Vector3f mov = new Vector3f(0,0,0);
            Vector3f aim = new Vector3f(-1,0.0001,0.0001);
                
            if(enemy == null)
            {
                this.setBotMovement(mov, aim, 100, PlayerMove.POSTURE_NORMAL);
                System.out.println(this.getName() + " no hay enemigos");
            }
            else 
            {
                System.out.println("enemy antes = " + enemyPos.getX() + " " + enemyPos.getY() + " " + enemyPos.getZ());
                System.out.println("enemy ahora = " + enemy.getOrigin().getX() + " " + enemy.getOrigin().getY() + " " + enemy.getOrigin().getZ());
                //Si el enemigo se ha movido
                if((enemy.getOrigin().getX() != enemyPos.getX()) || (enemy.getOrigin().getY() != enemyPos.getY()) || (enemy.getOrigin().getZ() != enemyPos.getZ()))
                {
                    System.out.println("nueva ruta");
                    actualWayPoint = 0;
                    //Obtenemos la nueva ruta
                    route = this.findShortestPath(enemy.getOrigin());
                    routeLength = route.length;
                    System.out.println("ruta = " + routeLength);
                    enemyPos.setX(enemy.getOrigin().getX());
                    enemyPos.setY(enemy.getOrigin().getY());
                    enemyPos.setZ(enemy.getOrigin().getZ());
                }
                else System.out.println("vieja ruta");
                targetPos.setX((int)route[actualWayPoint].getPosition().x);
                targetPos.setY((int)route[actualWayPoint].getPosition().y);
                targetPos.setZ((int)route[actualWayPoint].getPosition().z);                   
                arrived = makeMove(player.getPosition(),targetPos);
  
                if(arrived==1) 
                {
                    if(actualWayPoint < routeLength - 1) actualWayPoint++;
                }
   //             System.out.println(this.getName() + "Enemigo = " +enemy.getOrigin().getX() +  " " + enemy.getOrigin().getY() + " " + enemy.getOrigin().getZ());
                Vector3f vecPlay = new Vector3f(player.getPosition());
                Vector3f vecEne = new Vector3f(enemy.getOrigin());
  //              System.out.println("BOT = " + vecPlay.x + " " + vecPlay.y + " " + vecPlay.z);
  //              System.out.println("ENEMIGO = " + vecEne.x + " " + vecEne.y + " " + vecEne.z);
                //Si es visible ataca
                aim.set(targetPos.getX()-player.getPosition().getX(), targetPos.getY()-player.getPosition().getY(), targetPos.getZ()-player.getPosition().getZ());
                if(this.enemyVisible(player.getPosition(),mibsp, opponents, aim)!=null)
                {
                    aim = new Vector3f(enemy.getOrigin().getX() - player.getPosition().getX(),enemy.getOrigin().getY() - player.getPosition().getY(),enemy.getOrigin().getZ() - player.getPosition().getZ());
                    this.setBotMovement(mov, aim, 100, PlayerMove.POSTURE_NORMAL);
                    setAction(Action.ATTACK, true);
     //                   System.out.println("VISIBLE");
                }
                else 
                {
                    // this.setBotMovement(mov, aim, 100, PlayerMove.POSTURE_NORMAL);
                    setAction(Action.ATTACK, false);
                }


            }*/

            //Código automata
            
            if(longTermGoalPath == null)
            {
                longTermGoalPath = new Origin[world.getEntities(false).size()];
                lengthGoalPath = getLongTermGoalPath(longTermGoalPath,player.getPosition());
                countGoalPath = 0;
            }
            
            int battleStrategy;
            Vector3f aim = new Vector3f(0,0,0);
            targetPos = new Origin(0,0,0);
            nextWayPoint = new Origin(0,0,0);

            aim.set(targetPos.getX()-player.getPosition().getX(), targetPos.getY()-player.getPosition().getY(), targetPos.getZ()-player.getPosition().getZ());

            if(((enemy = this.visibleEnemy(player.getPosition(),mibsp, opponents, aim)) != null)&&((battleStrategy = decideBattle())!=0))
            {

                System.out.println("VISIBLE");
                

                System.out.println("BATTLE = "+battleStrategy);
                //if(battleStrategy == FIGHT)
                //{
                    //Perseguir
                    this.chaseEnemy();          
                    goal = false;
                //}
               //if(battleStrategy == RUNAWAY)
                //{
                    //Huir
                    
                //}
            }
            //No hay enemigo visible
            else
            {
                setAction(Action.ATTACK, false);
                //Si ya se ha cumplido el objetivo o es el principio obtenemos uno nuevo
      /*          if(lowHealth())
                {
                    System.out.println("A POR VIDA");
                    shortTermGoal = true;
                    actualWayPoint = 0;
                    goalPos = decideShortTermGoal();

                    route = this.findShortestPath(goalPos);
                    if(route == null) routeLength = 0;
                    else routeLength = route.length;
                }
                else*/ if(!goal)
                {
                    actualWayPoint = 0;
                    System.out.println("OBJETIVO = " + longTermGoalPath[countGoalPath].getX() + " " + longTermGoalPath[countGoalPath].getY() + " "+ longTermGoalPath[countGoalPath].getZ());
                    route = this.findShortestPath(longTermGoalPath[countGoalPath]);
                    if(route == null) routeLength = 0;
                    else routeLength = route.length;
                    goal = true;
                    countGoalPath++;
                }
                if(routeLength > 0)
                {
                    //Obtener el siguiente wayPoint
                    nextWayPoint.setX((int)route[actualWayPoint].getPosition().x);
                    nextWayPoint.setY((int)route[actualWayPoint].getPosition().y);
                    nextWayPoint.setZ((int)route[actualWayPoint].getPosition().z);             
                
                    //System.out.println("SIG = " + nextWayPoint.getX() + " " + nextWayPoint.getY() + " "+ nextWayPoint.getZ());
                
                    arrived = makeMove(player.getPosition(),nextWayPoint,null);
                }               
                if((arrived==1)||(routeLength == 0))
                {
                    //System.out.println("LLEGO" + actualWayPoint + " " + routeLength);
                    if(actualWayPoint < routeLength - 1) actualWayPoint++;
                    else 
                    {
 
                        if(!shortTermGoal) 
                        {
                            goal = false;
                        }
                    }
                }
                if(lengthGoalPath == countGoalPath) countGoalPath = 0;
            }

        }
        
                     
        //Establece la direccion de movimiento y devuelve si se ha llegado
        //al objetivo o no
        private int makeMove(Origin sourcePos,Origin targetPos,Vector3f aim)
        {
            //Obtener la diferencia
            int X = targetPos.getX() - sourcePos.getX();
            int Y = targetPos.getY() - sourcePos.getY();
            double stepX;
            double stepY;
            
            //Establecer el incremento para llegar al objetivo
            if(X > 0) stepX = 1;
            else if(X < 0) stepX = -1;
            else stepX = 0.0001;
            
            if(Y > 0) stepY = 1;
            else if(Y < 0) stepY = -1;
            else stepY = 0.0001;
            
            Vector3f dirMov = new Vector3f(0,0,0);
            dirMov.set((int)stepX,(int)stepY,0);

            if(aim != null) setBotMovement(dirMov, aim, 1, PlayerMove.POSTURE_NORMAL);
            else setBotMovement(dirMov, dirMov, 1, PlayerMove.POSTURE_NORMAL);
            
            if((X <= 50) && (X >= -50) && (Y <= 50) && (Y >= -50)) return 1;
            else return 0;
        }
        
        
        private void getAimingVector(Vector3f aimingVector,Origin enemyPos)
        {
            aimingVector.set(new Vector3f(enemyPos.getX() - player.getPosition().getX(),enemyPos.getY() - player.getPosition().getY(),enemyPos.getZ() - player.getPosition().getZ()));
        }
        
        
        
        private Entity visibleEnemy(Origin playerPos,BSPParser bsp,Vector opponents,Vector3f aim)
        {
            Vector3f aimEnemy = new Vector3f();
            for(int i=0;i<opponents.size();i++)
            {
                enemy = (Entity)opponents.get(i);
                if (enemy.getCTFTeamNumber() == this.getCTFTeamNumber()) continue;
                aimEnemy.set(enemy.getOrigin().getX()-playerPos.getX(), enemy.getOrigin().getY()-playerPos.getY(), enemy.getOrigin().getZ()-playerPos.getZ());
                aimEnemy.angle(aim);
                if((aimEnemy.angle(aim) < 90) && (bsp.isVisible(playerPos.toVector3f(), enemy.getOrigin().toVector3f())))
                {
                    return enemy;
                }
            }
            return null;
        }
                     
        private boolean visibleEnemy(Origin playerPos,BSPParser bsp,Vector3f aim)
        {
            Vector3f aimEnemy = new Vector3f();
            aimEnemy.set(enemy.getOrigin().getX()-playerPos.getX(), enemy.getOrigin().getY()-playerPos.getY(), enemy.getOrigin().getZ()-playerPos.getZ());
            aimEnemy.angle(aim);
            if((aimEnemy.angle(aim) < 90) && (bsp.isVisible(playerPos.toVector3f(), enemy.getOrigin().toVector3f())))
            {
                return true;
            }
            return false;
        }
        
        
        private int getNumberVisibleEnemies(Origin playerPos,BSPParser bsp,Vector opponents,Vector3f aim)
        {
            Vector3f aimEnemy = new Vector3f();
            int n = 0;
            for(int i=0;i<opponents.size();i++)
            {
                enemy = (Entity)opponents.get(i);
                aimEnemy.set(enemy.getOrigin().getX()-playerPos.getX(), enemy.getOrigin().getY()-playerPos.getY(), enemy.getOrigin().getZ()-playerPos.getZ());
                aimEnemy.angle(aim);
                if((aimEnemy.angle(aim) < 90) && (bsp.isVisible(playerPos.toVector3f(), enemy.getOrigin().toVector3f())))
                {
                    n++;
                }
            }
            return n;
        }       

        private void chaseEnemy()
        {
            System.out.println("CHASE ENEMY");
            Vector3f aim = new Vector3f();
            setAction(Action.ATTACK, true);
 //           while(this.visibleEnemy(player.getPosition(),mibsp, aim))
 //           {
                System.out.println("PERSIGUIENDO");
                this.getAimingVector(aim,enemy.getOrigin());
                this.makeMove(player.getPosition(), enemy.getOrigin(), aim);
 //           } 
        }
        
        
        
        private boolean lowHealth()
        {
            if(player.getHealth() < 60) return true;
            else return false;
        }

        int GET_LIFE = 1;
        int GET_ARMOUR = 2;
             
        //Decide el objetivo a corto plazo
        private Origin decideShortTermGoal()
        {
            int[] entitiesToGet = {Inventory.HEALTH};
            Entity[] entities = world.getEntities(entitiesToGet);
            Entity closestEntity = getClosestEntity(entities);
            return closestEntity.getOrigin();
            /*int res = -1;
            try {

		engine = new Rete();

                engine.batch(rutas.Jess_path);
                engine.eval("(reset)");
                engine.assertString("(currentPosition " + player.getPosition().getX() + " " + player.getPosition().getY() + " " + player.getPosition().getZ() + ")");
                engine.assertString("(health " + player.getHealth() + ")");

                engine.assertString("(armor " + player.getArmor() +  ")");
                
                engine.assertString("(healthLowLimit " + healthLowLimit + ")");
                engine.assertString("(armorLowLimit " + armorLowLimit + ")");
                engine.assertString("(healthHighLimit " + healthHighLimit + ")");
                engine.assertString("(armorHighLimit " + armorHighLimit + ")");

                engine.assertString("(items Health BigHealth Armor BigArmor)");
                engine.assertString("(itemsDistance 30 40 20 50)");
                
                engine.assertString("(weapons MG RL RG)");
                engine.assertString("(ammo 30 6 -1)");
                engine.assertString("(wDistance 100 30 99)");
                
                
                engine.run();

                res = engine.eval("?*ACTION*").intValue(null);

            } catch (JessException je) {
                System.out.println(je.toString());
            }
           
            if (res == GET_LIFE) {    

                return new Origin(1900, 1240, 1048); //Vida que obtuve manualmente         
            }
            else if (res == GET_ARMOUR) {
                return this.findClosestItem(Inventory.ARMOR_SHARD).getPosition().toOrigin();
            }

            return this.findClosestItem(Inventory.JACKET_ARMOR).getPosition().toOrigin();*/

        }
        
        private Entity getClosestEntity(Entity[] entities)
        {
            double min = 1000000;
            int index = 0;
            double wayDist;
            for(int i=0;i<entities.length;i++)
            {
                if((wayDist = waypointDistance(this.findShortestPath(entities[i].getOrigin()))) < min)
                {
                    min = wayDist;
                    index = i;
                }
            }
            return entities[index];
        }
        
        private double waypointDistance(Waypoint[] way)
        {
            double acum = this.euclideanDistance(player.getPosition(),way[0].getPosition().toOrigin());
            for(int i=0;i<way.length;i++)
            {
                if(i<way.length - 1)
                {
                    acum = acum + euclideanDistance(way[i].getPosition().toOrigin(),way[i+1].getPosition().toOrigin());
                }
            }
            return acum;
        }
        
        private Origin getClosestItemLocation(int itemIndex) {
            return null;
        }
        
        
    	private int [] getWeaponStats(int Index)
    	{
    		int maxAmmo = 0, directDamage = 1, accuracy = 2, CD = 3;
    		int [] res = new int[4];
			res[CD] = WEAPON_CDS[Index-7];
			res[maxAmmo] = weaponsIndex[Index-7][2];
			res[directDamage] = WEAPON_DAMAGE[Index-7];
			res[accuracy] = WEAPON_ACCURACY[Index-7];
    		return res;
    	}
    	
    	private int max(int [] vector){
    		int max = 0;
    		for(int i=0;i<vector.length;i++){
    			if (vector[i] > max) max = vector[i];
    		}
    		return max;
    	}
    	int DCDALowLimit = 0;
    	int DCDAHighLimit = 100;
    	
    	int AEdLowLimit = 500;
    	int AEdHighLimit = 1000;
    	
    	int ammoLowLimit = 40;
    	int ammoHighLimit = 70;
    	
    	//DMG -> 0 - 200
    	//CD -> 0 - 24
    	//accuracy -> 0 - 10
    	float damageCDAccuracyHeuristic(int directDamage, int CD, int accuracy){
    		float accuracyFactor = (float) (100.0*accuracy);
    		float CDFactor = (float) (100.0*(CD+0.01)/24.0);
    		float damageFactor = (float) (100*Math.log(directDamage)/Math.log(200.0));
    		//System.out.println("ACC "+accuracyFactor+" CDFactor "+CDFactor+" damageFactor "+damageFactor);
    		return (float) (0.2*accuracyFactor-0.5*CDFactor+0.3*damageFactor);
    	}
    		
    	float accuracyEnemyDistanceHeuristic(int enemyDistance, int CD, int accuracy){
    		return 1;
    	}
    	
    	private float getHeuristicValue(int weaponIndex,int maxAmmo, int directDamage, int accuracy, int CD, int enemyDistance){
    		int res = -1;
    		float actualAmmo;
    		//System.out.println("Arma = "+weaponIndex+"Tipo de municion "+ weaponsIndex[weaponIndex-7][0]);
    		
    		Inventory inv = world.getInventory();
    		if (weaponsIndex[weaponIndex-7][0] == -1) return 1;
    		else{
    				actualAmmo = ((float)inv.getCount(weaponsIndex[weaponIndex-7][0]))/(float)maxAmmo;
    		}
            
            float DCDA = damageCDAccuracyHeuristic(directDamage, CD, accuracy);
            float AEd = accuracyEnemyDistanceHeuristic(enemyDistance, CD, accuracy);
            //System.out.println("HEURISTIC VALUES: DCDA = "+DCDA+" AEd = "+AEd+" actualAmmo = "+actualAmmo+ " VALUE "+(float) ((0.85*DCDA+0.15*AEd)*actualAmmo));
            return (float) ((0.85*DCDA+0.15*AEd)*actualAmmo);
    		
    		
    		/*try {
            	engine = new Rete();
                engine.batch(rutas.Jess2_path);
                engine.eval("(reset)");

                float DCDA = damageCDAccuracyHeuristic(directDamage, CD, accuracy);
                float AEd = accuracyEnemyDistanceHeuristic(directDamage, CD, accuracy);

                engine.assertString("(weaponID "+weaponIndex+")");
                
                if (DCDA < DCDALowLimit) engine.assertString("(DCDA LOW)");
                else if (DCDA < DCDAHighLimit) engine.assertString("(DCDA MED)");
                else if (DCDA > DCDAHighLimit) engine.assertString("(DCDA HIGH)");
                
                if (AEd < AEdLowLimit) engine.assertString("(AEd LOW)");
                else if (AEd < AEdHighLimit) engine.assertString("(AEd MED)");
                else if (AEd > AEdHighLimit) engine.assertString("(AEd HIGH)");
                
                if (actualAmmo < ammoLowLimit) engine.assertString("(ammo LOW)");
                else if (actualAmmo < ammoHighLimit) engine.assertString("(ammo MED)");
                else if (actualAmmo > ammoHighLimit) engine.assertString("(ammo HIGH)");
                
                engine.run();

                res = engine.eval("?*ACTION*").intValue(null);
                System.out.println("res = " + res);
                return res;

            } catch (JessException je) {
                System.out.println(je.toString());
            }*/
    		
    		/*BLASTER = 7, SHOTGUN = 8, SUPER_SHOTGUN = 9,
    				MACHINEGUN = 10, CHAINGUN = 11, GRENADES = 12, GRENADE_LAUNCHER = 13,
    				ROCKET_LAUNCHER = 14, HYPERBLASTER = 15, RAILGUN = 16, BFG10K = 17,
    				
    				
    				
    				SHELLS = 18, BULLETS = 19, CELLS = 20, ROCKETS = 21, SLUGS = 22;*/
    		//actualAmmo, Damage, Accuracy, CD, Distance
    		
    		/*switch (weaponIndex){
    			case BLASTER:
    				if(actualAmmo < 20){
        				
    				}
    				if(actualAmmo < 50){
        				
    				}
    				if(actualAmmo < 80){
        				
    				}
    				if(actualAmmo < 100){
        				
    				}
    				break;
    			case SHOTGUN:
    				break;
    			case SUPER_SHOTGUN:
    				break;
    			case MACHINEGUN:
    				break;
    			case CHAINGUN:
    				break;
    			case GRENADES:
    				break;
    			case GRENADE_LAUNCHER:
    				break;
    			case ROCKET_LAUNCHER:
    				break;
    			case HYPERBLASTER:
    				break;
    			case RAILGUN:
    				break;
    		}*/
            //return res;
    	}
    	void getWeaponIndexes(Vector<Integer> vector){
    		for (int i=0;i<11;i++){
    			int index = i+7;
    			if (this.hasItem(index)) vector.add(index);
    		}
    	}
    	float actualWeaponHeuristic = 0;
        private int decideBestWeapon(Player player){
         //System.out.println("OBTENIENDO VECTOR DE ARMAS");
         java.util.Vector<Integer> weaponsVector = new Vector<Integer>();
         getWeaponIndexes(weaponsVector);
         //System.out.println("VECTOR OBTENIDO");
         //System.out.println("TENGO "+weaponsVector.size()+" ARMAS");
         int maxAmmo = 0, directDamage = 1, accuracy = 2, CD = 3, weaponIndex = 7;
         int [] stats = new int[4];
         int bestWeapon = 7;
         //System.out.println("PETADO?");
         float [] preferencias = new float[weaponsVector.size()];
         for(int i=0;i<weaponsVector.size();i++){
         weaponIndex = weaponsVector.get(i);
         stats = getWeaponStats(weaponsVector.get(i));
         //Hay que obtener la distancia al enemigo para considerar.
             preferencias[i] = getHeuristicValue(weaponIndex, stats[maxAmmo], stats[directDamage], stats[accuracy], stats[CD], 100);
             if ((i!=0)&&(preferencias[i]>preferencias[i-1])){
             bestWeapon = weaponIndex;
             actualWeaponHeuristic = preferencias[i];
             }
             //System.out.println("TENGO "+weaponIndex+ " Value = "+ preferencias[i]);
         }
        
         return weaponIndex;
        }
        float weaponValueLowLimit = 0;
        float weaponValueHighLimit = 10;
        //Enemy tiene la entidad del enemigo
        private int decideBattle()
        {
                    /*
            Factores:
            	- Vida actual +
            	- Armadura actual +
            	- Arma actual (Valoracion sobre ella)
            	- Municion actual
            	- Segunda "mejor" arma
            	- municion de Segunda "mejor" arma
            	- Distancia al objetivo
            	- Si el objetivo esta mirandonos o no
            
        */
        	
        	//System.out.println("DECIDING WEAPON");
        	int weapon = decideBestWeapon(player);
        	this.changeWeaponByInventoryIndex(weapon);
        	System.out.println("WEAPON DECIDED");
        	//Asumimos que hemos cambiado a la mejor arma que podr�amos tener.
        	int res = -1;
            try {
            	engine = new Rete();
                engine.batch(rutas.Jess2_path);
                engine.eval("(reset)");
                //Vida Actual
                int health = player.getHealth();
                
                if (health < healthLowLimit){
                	engine.assertString("(healthLevel LOW)");
                	health = 0;
                }
                else if (health < healthHighLimit){
                	engine.assertString("(healthLevel MED)");
                	health = 1;
                }
                else if (health > healthHighLimit){
                	engine.assertString("(healthLevel HIGH)");
                	health = 2;
                }
                //Armadura Actual
                int armor= player.getArmor();
                if (armor < armorLowLimit){
                	engine.assertString("(armorLevel LOW)");
                	armor = 0;
                }
                else if (armor < armorHighLimit){
                	engine.assertString("(armorLevel MED)");
                	armor = 1;
                }
                else if (armor > armorHighLimit){
                	engine.assertString("(armorLevel HIGH)");
                	armor = 2;
                }

                
                // Valoraci�n del arma del enemigo
                int enemyWeaponIndex = enemy.getWeaponInventoryIndex();
            	int [] stats = new int[4];
                stats = getWeaponStats(enemyWeaponIndex);
            	int maxAmmo = 0, directDamage = 1, accuracy = 2, CD = 3; 
                float enemyWeaponValue = getHeuristicValue(enemyWeaponIndex, stats[maxAmmo], stats[directDamage], stats[accuracy], stats[CD], 100);

                //Valor del arma actual con respecto a la del enemigo
                //ours -> Low
                if (actualWeaponHeuristic < weaponValueLowLimit){
                    if (enemyWeaponValue < weaponValueLowLimit) engine.assertString("(advantage 0)");
                    else if (enemyWeaponValue < weaponValueHighLimit) engine.assertString("(advantage -1)");
                    else if (enemyWeaponValue > weaponValueHighLimit) engine.assertString("(advantage -2)");
                }
                //ours -> Med
                else if (actualWeaponHeuristic < weaponValueHighLimit){
                    if (enemyWeaponValue < weaponValueLowLimit) engine.assertString("(advantage 1)");
                    else if (enemyWeaponValue < weaponValueHighLimit) engine.assertString("(advantage 0)");
                    else if (enemyWeaponValue > weaponValueHighLimit) engine.assertString("(advantage -1)");
                }
                //ours -> High
                else if (actualWeaponHeuristic > weaponValueHighLimit){
                    if (enemyWeaponValue < weaponValueLowLimit) engine.assertString("(advantage 2)");
                    else if (enemyWeaponValue < weaponValueHighLimit) engine.assertString("(advantage 1)");
                    else if (enemyWeaponValue > weaponValueHighLimit) engine.assertString("(advantage 0)");
                }

               // System.out.println("Health "+health+" Armor "+armor);

                engine.run();

                res = engine.eval("?*ACTION*").intValue(null);
                System.out.println("BATTLE res = " + res);

            } catch (Exception je) {
                System.out.println(je.toString());
                return 0;
            }
            return res;
        }
 
        
/*

        
        private double evalGoal(GoalState state)
        {
            double factorHealth = state.health - player.getHealth();
            double factorHealthDistance;
            double factorDistance = this.euclideanDistance(state.position,player.getPosition());
            double factorArmor = player.getArmor();
            double factorArmorDistance;
            double factorAmmo;
            double factorAmmoDistance;
            boolean haveHyper = this.hasItem(Inventory.HYPERBLASTER);
            double factorHyperDistance;
            if(haveHyper) factorAmmo = state.ammo - player.getAmmo();
            else factorAmmo = 0;
            Waypoint healthWay = this.findClosestItem(Inventory.HEALTH);
            Waypoint armorWay = this.findClosestItem(Inventory.ARMOR_SHARD);
            Waypoint hyperWay = this.findClosestItem(Inventory.HYPERBLASTER);
            Waypoint ammoWay = this.findClosestItem(Inventory.CELLS);
            if(healthWay != null)
            {
                factorHealthDistance = state.healthDistance-this.euclideanDistance(player.getPosition(),healthWay.getPosition().toOrigin());
            }
            else factorHealthDistance = 0;
            
            if(armorWay != null)
            {
                factorArmorDistance = state.armorDistance-this.euclideanDistance(player.getPosition(),armorWay.getPosition().toOrigin());
            }
            else factorArmorDistance = 0;
            
            if(ammoWay != null)
            {
                factorAmmoDistance = state.ammoDistance-this.euclideanDistance(player.getPosition(),ammoWay.getPosition().toOrigin());
            }
            else factorAmmoDistance = 0;
            if(hyperWay != null)
            {
                factorHyperDistance = state.hyperDistance-this.euclideanDistance(player.getPosition(),hyperWay.getPosition().toOrigin());
            }
            else factorHyperDistance = 0;
            return 1;

        }*/
        
        private double euclideanDistance(Origin o1,Origin o2)
        {
            return Math.sqrt((o1.getX()-o2.getX())*(o1.getX()-o2.getX())+(o1.getY()-o2.getY())*(o1.getY()-o2.getY()));
        }
        
        
        
        
        private int getLongTermGoalPath(Origin[] longTermGoalPath,Origin actualPos)
        {
            //System.out.println("getLongTermGOalPath");
            Vector totalEntities = world.getEntities(false);
            Origin[] interestingEntities = new Origin[totalEntities.size()];
            int count = 0;
            Entity entity;
            
            //armorShard,cells,bullets,shells
            boolean[] redundantItems = {false,false,false,false};
            
            for(int i=0;i<totalEntities.size();i++)
            {
                entity = (Entity)totalEntities.get(i);
                if(isAnInterestingEntity(redundantItems,entity)) 
                {
                    
                    interestingEntities[count] = new Origin(entity.getOrigin().getX(),entity.getOrigin().getY(),entity.getOrigin().getZ());
                    count++;
                }
            }

            //System.out.println("YA LOS TENGO Y SON " + count);
            int[] result = new int[count];
            this.theOriginOfSpecies(result,interestingEntities,actualPos,120, count,0.5);
            //System.out.println("RESULTADO");
            //for(int i=0;i<result.length;i++) System.out.print(result[i] + " ");
            //System.out.println("");
            for(int i=0;i<result.length;i++)
            {
                longTermGoalPath[i] = new Origin(interestingEntities[result[i]].getX(),interestingEntities[result[i]].getY(),interestingEntities[result[i]].getZ());
            }
            return count;
        }
        
        
        private boolean isAnInterestingEntity(boolean[] redundantItems,Entity entity)
        {
            if(entity.getInventoryIndex() == Inventory.COMBAT_ARMOR) return true;
            if(entity.getInventoryIndex() == Inventory.JACKET_ARMOR) return true;
            if((redundantItems[0] == false) && (entity.getInventoryIndex() == Inventory.ARMOR_SHARD))
            {
                redundantItems[0] = true;
                return true;
            }
            if(entity.getInventoryIndex() == Inventory.HYPERBLASTER) return true;
            if((redundantItems[1] == false) && (entity.getInventoryIndex() == Inventory.CELLS))
            {
                redundantItems[1] = true;
                return true;
            }
            if(entity.getInventoryIndex() == Inventory.SHOTGUN) return true;
            if((redundantItems[2] == false) && (entity.getInventoryIndex() == Inventory.SHELLS))
            {
                redundantItems[2] = true;
                return true;
            }
            if(entity.getInventoryIndex() == Inventory.MACHINEGUN) return true;
            if((redundantItems[3] == false) && (entity.getInventoryIndex() == Inventory.BULLETS)) 
            {
                redundantItems[3] = true;
                return true;
            }
            //if(entity.getInventoryIndex() == Inventory.RAILGUN) return true;
            return false;
        }
        
        ///////////////////////////////////////
        //Funciones para el algoritmo genetico
        //////////////////////////////////////
        private int theOriginOfSpecies(int[] evolutionWinner,Origin[] specimenAttributes,Origin actualPos,int population,int nAttributes,double mutationProb)
        {
            System.out.println("EMPIEZA ALGORITMO GENETICO");
            int iterations = 0;
            double[] fitness = new double[population];
            double[] reproductionRate = new double[population];
            int[][] specimenSet = new int[population][nAttributes];
            int[][] offsprings = new int[population][nAttributes];
            double max = 100000;
            double[] info = new double[2];
            int[][] ancestors = new int[population][nAttributes];
            WaypointDistances[] waypointDistances = new WaypointDistances[nAttributes+1];
            for(int i=0;i<nAttributes;i++) waypointDistances[i] = new WaypointDistances(specimenAttributes[i],specimenAttributes,nAttributes,wpMap,false);
            waypointDistances[nAttributes] = new WaypointDistances(actualPos,specimenAttributes,nAttributes,wpMap,true);
            getInitialSpecimenSet(specimenSet,nAttributes);
            System.out.println("specimenSet Inicial");
           /* for(int i=0;i<population;i++)
            {
                for(int j=0;j<nAttributes;j++)
                {
                    System.out.print(specimenSet[i][j] + " ");
                }
                System.out.println("");
            }*/
            evalSpecimenSet(fitness,specimenSet,population,actualPos,specimenAttributes,nAttributes,waypointDistances); 
            System.out.println("fitness Inicial");
            /*for(int i=0;i<population;i++)
            {
                System.out.println(fitness[i]);
            }*/
            while(iterations < 100000)
            {
                info = max(fitness);
       //         System.out.println("max = " + info[0]);
                max = info[0];
                getNaturalSelection(reproductionRate,fitness);
                /*if(iterations == 0)
                {
                    System.out.println("reproduction Rate");
                    for(int i=0;i<population;i++)
                    {
                        System.out.println(reproductionRate[i]);;
                    }
                }*/
                reproduceSpecimenSet(offsprings,specimenSet,reproductionRate);
       /*         System.out.println("descendientes");
                for(int i=0;i<population;i++)
                {
                    System.out.println(offsprings[i][0] + " " + offsprings[i][1] + " " + offsprings[i][2] + " " + offsprings[i][3]+ " " + offsprings[i][4] + " " + offsprings[i][5]);
                } */
                if(evolSpecie(offsprings,ancestors)) break;
                else noCountryForOldMen(ancestors,offsprings); 
                mutateSpecimens(offsprings,mutationProb);
       /*         System.out.println("descendientes mutantes");
                for(int i=0;i<population;i++)
                {
                    System.out.println(offsprings[i][0] + " " + offsprings[i][1] + " " + offsprings[i][2] + " " + offsprings[i][3]+ " " + offsprings[i][4] + " " + offsprings[i][5]);
                }*/
                evalSpecimenSet(fitness,offsprings,population,actualPos,specimenAttributes,nAttributes,waypointDistances);
                noCountryForOldMen(specimenSet,offsprings);
        /*        System.out.println("fitness");
                for(int i=0;i<population;i++)
                {
                    System.out.println(fitness[i]);
                }*/
                iterations++;
            }
            System.out.println("Fin de la evolucion");
            System.out.println("specimenSet FINAL");
            for(int i=0;i<population;i++)
            {
                for(int j=0;j<nAttributes;j++)
                {
                    System.out.print(specimenSet[i][j] + " ");
                }
                System.out.println("");
            } 
            System.out.println("fitness Final = " + fitness[(int)info[1]]);
            
            for(int i=0;i<specimenSet[0].length;i++) evolutionWinner[i] = specimenSet[(int)info[1]][i];
            System.out.println("iterations = " + iterations);
            return iterations;
        }
        
        
        private void getInitialSpecimenSet(int[][] specimenSet,int nAttributes)
        {
            int count;
            int element;
            boolean first;
            int[] specimen = new int[specimenSet[0].length];
            for(int i=0;i<specimenSet.length;i++)
            {
                first = true;
                while((first==true) || equalSpecimen(specimenSet,specimen))
                {
                    first = false;
                    count = 0;
                    for(int j=0;j<nAttributes;j++)
                    {
                        element = getElementNotDuplicate(specimen,count,nAttributes);
                        specimen[j] = element;
                        count++;
                    }
                }
                for(int j=0;j<nAttributes;j++)
                {
                    specimenSet[i][j] = specimen[j];
                }
            }
        }
        
        private boolean  equalSpecimen(int[][] specimenSet,int[] specimen)
        {

           // System.out.println("espec to compare" + specimen[0] + " " + specimen[1] + " " + specimen[2] + " ");
            boolean distinto;   
            for(int i=0;i<specimenSet.length;i++)
            {
                distinto = false;
                for(int j=0;j<specimenSet[0].length;j++)
                {
                    if(specimenSet[i][j] != specimen[j]) distinto = true;
                }
                if(distinto == false) return true;
            }
            return false;
        }
        
        private int getElementNotDuplicate(int[] vector,int count,int nAttributes)
        {
            int random = (int)((Math.random() * 100)%(nAttributes-1));
            while(isInVector(random,vector,count))
            {
                random = (int)((Math.random() * 100)%nAttributes);
                //System.out.println("random = " + random);
            }
            return random;
        }
        
        
        private boolean isInVector(int number,int[] vector,int count)
        {
            for(int i=0;i<count;i++)
            {
                if(number == vector[i]) return true;
            }
            return false;
        }
        
        private void evalSpecimenSet(double[] fitness,int[][] specimenSet,int population,Origin actualPos,Origin[] specimenAttributes,int nAttributes,WaypointDistances[] waypointDistances)
        {
            for(int i=0;i<population;i++)
            {
                fitness[i] = evalSpecimen(specimenSet[i],actualPos,specimenAttributes,nAttributes,waypointDistances);
            }
        }
        
        private double evalSpecimen(int[] specimen,Origin actualPos,Origin[] specimenAttributes,int nAttributes,WaypointDistances[] waypointDistances)
        {
            WaypointDistances wpd = getWaypointDistances(actualPos,waypointDistances);
            double distance = wpd.getWaypointDistanceToOrigin(specimenAttributes[specimen[0]]);
            for(int i=0;i<nAttributes;i++)
            {
                if(i+1<nAttributes) 
                {
                    wpd = getWaypointDistances(specimenAttributes[specimen[i]],waypointDistances);
                    distance = distance + wpd.getWaypointDistanceToOrigin(specimenAttributes[specimen[i+1]]);
                }
            }
            return Math.pow(1000,40)/Math.pow(distance,25);
        }
        
        
        WaypointDistances getWaypointDistances(Origin or,WaypointDistances[] wpd)
        {
            for(int i=0;i<wpd.length;i++)
            {
                if(wpd[i].origin.equals(or)) return wpd[i];
            }
            return null;
        }

        
        private double[] max(double[] vector)
        {
            double max = 0;
            int index = 0;
            for(int i=0;i<vector.length;i++)
            {
                if(vector[i] > max)
                {
                    max = vector[i];
                    index = i;
                }
            }
            double[] res = {max,index};
            return res;
        }
        
        private void getNaturalSelection(double[] reproductionRate,double[] fitness)
        {
            double fitnessSum = sum(fitness);
            /*double[] inverseFitness = new double[fitness.length];
            asignInverseValues(inverseFitness,fitness,fitnessSum);
            double inverseFitnessSum = sum(inverseFitness);*/
            for(int i=0;i<reproductionRate.length;i++)
            {
                //reproductionRate[i] = inverseFitness[i]/inverseFitnessSum;
                reproductionRate[i] = fitness[i]/fitnessSum;
            }
        }
        
        private double sum(double[] fitness)
        {
            double acum = 0;
            for(int i=0;i<fitness.length;i++)
            {
                acum = acum + fitness[i];
            }
            return acum;
        }
        
        private void asignInverseValues(double[] inverseFitness,double[] fitness,double fitnessSum)
        {
            for(int i=0;i<inverseFitness.length;i++)
            {
                inverseFitness[i] = fitnessSum - fitness[i];
            }
        }
        
        
        private void reproduceSpecimenSet(int[][] offspringSet,int[][] specimenSet,double[] reproductionRate)
        {
            int[] male = new int[specimenSet[0].length];
            int[] female = new int[specimenSet[0].length];
            int[][] children = new int[2][specimenSet[0].length];
            int[][] combinations = new int[specimenSet.length][2];
            int countCombs = 0;
            double rateGen;
            for(int i=0;i<specimenSet.length;i+=2)
            {
                rateGen = getSpecimensToProcreate(male,female,specimenSet,reproductionRate,combinations,countCombs);
      //          System.out.println("rateGen " + rateGen);
                reproduceSpecimens(children,male,female,rateGen);
 /*               System.out.println("children " + children[0][0] + " " + children[0][1] + " " + children[0][2] + " " + children[0][3]+ " " + children[0][4] + " " + children[0][5]);
                System.out.println("children " + children[1][0] + " " + children[1][1] + " " + children[1][2] + " " + children[1][3]+ " " + children[1][4] + " " + children[1][5]);
          */    
                for(int j=0;j<specimenSet[0].length;j++)
                {
                    offspringSet[i][j] = children[0][j];
                    offspringSet[i+1][j] = children[1][j];
                }
                countCombs++;

            }      
        }
        
        private double getSpecimensToProcreate(int[] male,int[] female,int[][] specimenSet,double[] reproductionRate,int[][] combinations,int countCombs)
        {
            double[] acumRate = new double[reproductionRate.length];
            double acum = 0;
            int indexMale = 0;
            int indexFemale = 0;
            int[] indexes = new int[2];
            boolean first = true;
    //        System.out.println("getspecimenstoprocreate");        
            for(int i=0;i<acumRate.length;i++)
            {
                acumRate[i] = acum + reproductionRate[i];
                acum = acumRate[i];
            }
    /*        System.out.println("acumrate");
            for(int i=0;i<acumRate.length;i++)
            {
                System.out.println(acumRate[i]);
            }*/
            while((first == true) || usedCombination(indexMale,indexFemale,combinations,countCombs))
            {
    //            System.out.println("obtener pareja");
                first = false;
                getTwoSpecimen(indexes,acumRate);
                indexMale = indexes[0];
                indexFemale = indexes[1];
            } 
            for(int i=0;i<male.length;i++) 
            {
                male[i] = specimenSet[indexMale][i];
                female[i] = specimenSet[indexFemale][i];
            }
            
   /*     System.out.println("male " + male[0] + " " + male[1] + " " + male[2] + " " + male[3] + " " + male[4] + " " + male[5]);
          System.out.println("female " + female[0] + " " + female[1] + " " + female[2] + " " + female[3] + " " + female[4] + " " + female[5]);
          */  combinations[countCombs][0] = indexMale;
            combinations[countCombs][1] = indexFemale;
            return reproductionRate[indexMale]/(reproductionRate[indexMale]+reproductionRate[indexFemale]);
        }
        
        
        private boolean usedCombination(int indexMale,int indexFemale,int[][] combinations,int countCombs)
        {
            for(int i=0;i<countCombs;i++)
            {
                if((combinations[i][0] == indexMale) && (combinations[i][1] == indexFemale) || (combinations[i][1] == indexMale) && (combinations[i][0] == indexFemale))
                {
                    return true;
                }
            }
            return false;
        }
        
        private void getTwoSpecimen(int[] indexes,double[] acumRate)
        {
            double random_1;
            double random_2;
            int index_1 = 0;
            int index_2 = 0;
            while(index_1 == index_2)
            {
                random_1 = Math.random();
                random_2 = Math.random();
      /*          System.out.println("random 1 = " + random_1);
                System.out.println("random 2 = " + random_2);*/
                for(int i=0;i<acumRate.length;i++)
                {
                    if(i > 0)
                    {
                        if((random_1 > acumRate[i-1]) && (random_1 <= acumRate[i]))
                        {
                            index_1 = i;
                        }
                        if((random_2 > acumRate[i-1]) && (random_2 <= acumRate[i]))
                        {
                            index_2 = i;
                        }                              
                    }
                    else
                    {
                        if((random_1 > 0) && (random_1 <= acumRate[i]))
                        {
                            index_1 = i;
                        }
                        if((random_2 > 0) && (random_2 <= acumRate[i]))
                        {
                            index_2 = i;
                        }                         
                    }
                }
            }        
            indexes[0] = index_1;
            indexes[1] = index_2;
        }

        
        private void reproduceSpecimens(int[][] children,int[] male,int[] female,double rateGen)
        {
  //          System.out.println("reproduce specimens");
            int indexPartGen = (int)Math.floor(rateGen*male.length);
  /*          System.out.println("male " + male[0] + " " + male[1] + " " + male[2] + " " + male[3] + " " + male[4] + " " + male[5]);
            System.out.println("female " + female[0] + " " + female[1] + " " + female[2] + " " + female[3] +  " " + female[4] + " " + female[5]);
            System.out.println("indexpartgen " + indexPartGen);*/
            for(int i=0;i<indexPartGen;i++)
            {               
                children[0][i] = male[i];
                count++;
            }
            for(int i=indexPartGen;i<male.length;i++)
            {
                children[0][i] = female[i];
            }
      //      System.out.println("children primero " + children[0][0] + " " + children[0][1] + " " + children[0][2] + " " + children[0][3]+ " " + children[0][4] + " " + children[0][5]);

            if(rateGen<0.5) changeDuplicateAttributes(children[0],male,indexPartGen,0);
            else changeDuplicateAttributes(children[0],female,indexPartGen,1);
  //          System.out.println("children primero sin duplis" + children[0][0] + " " + children[0][1] + " " + children[0][2] + " " + children[0][3]+ " " + children[0][4] + " " + children[0][5]);
            indexPartGen =  male.length - indexPartGen;
   //         System.out.println("indexpartgen " + indexPartGen);
            for(int i=0;i<indexPartGen;i++)
            {
                children[1][i] = female[i];
            }
            for(int i=indexPartGen;i<male.length;i++)
            {
                children[1][i] = male[i];
            }            
   //                         System.out.println("children segundo " + children[1][0] + " " + children[1][1] + " " + children[1][2] + " " + children[1][3] + " " + children[1][4] + " " + children[1][5]);
            if(rateGen<0.5) changeDuplicateAttributes(children[1],male,indexPartGen,1);
            else changeDuplicateAttributes(children[1],female,indexPartGen,0);
     //       System.out.println("children segundo sin duplis " + children[1][0] + " " + children[1][1] + " " + children[1][2] + " " + children[1][3]+ " " + children[1][4] + " " + children[1][5]);
        }
        
        private void changeDuplicateAttributes(int[] children,int[] bad_parent,int index,int bad_half)
        {
            int duplicate;
       //     System.out.println("duplicado = " + getDuplicate(children,bad_half));
            while((duplicate = getDuplicate(children,bad_half)) != -1)
            {
       /*         System.out.println("index_bad = " + index);
                System.out.println("bad_half = " + bad_half);
        */        children[duplicate] = getElementNotEqual(children,children[duplicate],bad_parent,index,bad_half);
         //       System.out.println("elemento nuevo = " + children[duplicate]);
            }
        }
        
        private int getDuplicate(int[] vector,int bad_half)
        {
        //    System.out.println(vector.length);
            for(int i=0;i<vector.length;i++)
            {
                for(int j=0;j<vector.length;j++)
                {
                    if((i!=j) && (vector[i] == vector[j]))
                    {
                        if(bad_half == 0)
                        {
                            if(i<j) return i;
                            else return j;
                        }
                        else
                        {
                            if(i<j) return j;
                            else return i;
                        }
                    }
                }
            }
            return -1;
        }
        
        private int getElementNotEqual(int[] children,int element,int[] vector,int index,int bad_half)
        {
            if(bad_half == 1)
            {
                for(int i=0;i<index+1;i++)
                {
                    if((element != vector[i]) && (!isInVector(vector[i],children,children.length))) return vector[i];
                }
            }
            else
            {
                for(int i=index;i<vector.length;i++)
                {
                    if((element != vector[i]) && (!isInVector(vector[i],children,children.length))) return vector[i];
                }
            }
            return -1;
        }
        
        private boolean evolSpecie(int[][] offsprings,int[][] ancestors)
        {
         //   System.out.println("Especie evolucionada??");
            for(int i=0;i<offsprings.length;i++)
            {
                for(int j=0;j<offsprings[0].length;j++)
                {
          //          System.out.println("descendiente = " + offsprings[i][j]  + " ancestor = " +  ancestors[i][j]);
                    if(offsprings[i][j] != ancestors[i][j]) return false;
                }
            }
            return true;
        }
        
        private void mutateSpecimens(int[][] offsprings,double mutationProb)
        {
            for(int i=0;i<offsprings.length;i++)
            {
                double mutacion = Math.random();
          //      System.out.println("mutacion prob = " + mutacion);
                if(mutacion < mutationProb)
                //if(Math.random() < mutationProb)
                {
                    int gen_1 = (int)((Math.random()*100)%offsprings[0].length);
                    int gen_2;
                    while((gen_2 = (int)((Math.random()*100)%offsprings[0].length)) == gen_1);
            /*        System.out.println("specimen = " + i);
                    System.out.println("gen_1 = " + gen_1);
                    System.out.println("gen_2 = " + gen_2);*/
                    int aux = offsprings[i][gen_1];
                    offsprings[i][gen_1] = offsprings[i][gen_2];
                    offsprings[i][gen_2] = aux;
                }
            }
        }
        
        private void noCountryForOldMen(int[][] specimenSet,int[][] offsprings)
        {
            for(int i=0;i<specimenSet.length;i++)
            {
                for(int j=0;j<specimenSet[0].length;j++)
                    specimenSet[i][j] = offsprings[i][j];
            }
        }
}

	
