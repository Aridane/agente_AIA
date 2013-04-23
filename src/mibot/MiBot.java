/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mibot;

import soc.qase.ai.waypoint.WaypointMapGenerator;
/**
 *
 * @author Cayetano
 */
public class MiBot {

    /**
     * @param args the command line arguments
     */
 
    static MiBotseMueve MiBot,MiBot2;  
    
    public static void main(String[] args) {
        // TODO code application logic here
        Init();	
    }
    
    public static void Init()
	{		
                        
        
		//Establece la ruta del quake2, necesaria para tener informaciÃ³n sobre los mapas.
		//Observa la doble barra
		String quake2_path="C:\\Users\\alvarin\\Desktop\\Dropbox\\Quinto\\AIA\\Quake\\quake2";
		//System.setProperty("QUAKE2", quake2_path); 

          
		//CreaciÃ³n del bot (pueden crearse mÃºltiples bots)
		MiBot = new MiBotseMueve("Venus","female/athena");
   //             MiBot2 = new MiBotseMueve("Marte","male/athena");
		MiBot.setMap(WaypointMapGenerator.generate("C:\\Users\\alvarin\\Desktop\\Dropbox\\Quinto\\AIA\\Quake\\quake2\\baseq2\\demos\\level.dm2", (float)0.2));
  //              MiBot2.setMap(WaypointMapGenerator.generate("C:\\Users\\alvarin\\Desktop\\Dropbox\\Quinto\\AIA\\Quake\\quake2\\baseq2\\demos\\level.dm2", (float)0.2));
		//Conecta con el localhost (el servidor debe estar ya lanzado para que se produzca la conexiÃ³n)
		MiBot.connect("127.0.0.1",27910);//Ejemplo de conexiÃ³n a la mÃ¡quina local
  //              MiBot2.connect("127.0.0.1",27910);//Ejemplo de conexiÃ³n a la mÃ¡quina local
 
             
	}
}
