import ea.*;

/**
 * @Description     Lokal Spiel-steuernde Klasse des Demo-Netzwerk-Spiels mit 2 Spielern.
 *                  Dient zugleich als Spiel-Client auf beiden Seiten des Demo-Netzwerk-Spiels.
 *                  Einer der Spieler startet zuerst eine Instanz eines MyGameServer.
 *                  (Der Server kann natuerlich auch auf einem dritten Rechner laufen).
 *                  Anschließend erzeugen beide Spieler je eine Instanz dieser Klasse (MyGame)
 *                  und verbinden sich mit dem Server.
 *
 * @Author          Mike Ganshorn
 *
 * @Version         1.1 (2015-02-13)
 *
 */
class MyGame
        extends ea.Game
{

    private Spielfigur[] figuren;
    private String name;
    private int meinIndex;
    private MyNetworkClient client;


    /**
     * Konstruktor der Klasse MyGame
     *
     * @param   ip      Die IP des Spiele-Servers
     * @param   port    Der Port des Spiele-Servers
     * @param   name    Der Name der Spielfigur dieser Spiel-Instanz
     */
    public MyGame( String ip , int port , String name )
    {
        super( 800 , 600 , "Engine-Alpha-Demo-Game" );

        this.figuren = new Spielfigur[2];
        this.name = name;
        this.client = new MyNetworkClient( ip , port );
        this.client.setzeSpiel( this );
        
        // Warte, bis die Verbindung steht
        this.client.warteAufVerbindung();
        
        System.out.println( "Done waiting." );
        
        this.client.sendeString( "anmelden:" + name );
    }


    /**
     * Methode zum Anmelden dieses Spiel-Clients beim Server.
     * Nach dem Anmelden bekommt jeder Spieler vom Server die Spielzuege des Gegeners (und die eigenen) uebertragen.
     *
     */
    public void anmelden()
    {
        this.client.sendeString("anmelden:" + name);
    }


    /**
     * Von Game geerbte Methode um auf Tastatur-Ereignisse reagieren zu koennen.    <br />
     *
     * Jedes Tastatur-Ereignis des lokalen Rechners wird an den Server uebertragen.
     * Der Server schickt als Reaktion darauf an jeden Client einen passenden "Befehl".
     * Erst der Erhalt eines solchen "Befehls" fuehrt bei den Clients zu Veraenderungen des Spielzustands.
     * (Nach diesem Prinzip laesst sich die Anzahl an Clients auch erhoehen.)
     *
     * Spielfigur steuern:                  Pfeiltasten                             <br />
     * Farbe der eigenen Figur aendern:     R: rot , G: gruen , Y: gelb , B: blau   <br />
     * Spieler abmelden:                    A
     *
     * @param   code    Code der gedrueckten Taste (als Konstanten in der Klasse Taste festgelegt)
     */
    @Override
    public void tasteReagieren( int code )
    {
        switch(code)
        {
            // Bewegung
            case Taste.RECHTS:
                this.client.sendeString( "bewegen:" + this.meinIndex + ":10:0" );
                break;
            case Taste.LINKS:
                this.client.sendeString( "bewegen:" + this.meinIndex + ":-10:0" );
                break;
            case Taste.OBEN: 
                this.client.sendeString( "bewegen:" + this.meinIndex + ":0:-10" );
                break;
            case Taste.UNTEN: 
                this.client.sendeString( "bewegen:" + this.meinIndex + ":0:10" );
                break;
                
            // Farbwechsel    
            case Taste.R:
                this.client.sendeString( "farbwechsel:" + this.meinIndex + ":rot" );
                break;
            case Taste.G:
                this.client.sendeString( "farbwechsel:" + this.meinIndex + ":gruen" );
                break;
            case Taste.Y:
                this.client.sendeString( "farbwechsel:" + this.meinIndex + ":gelb" );
                break;
            case Taste.B:
                this.client.sendeString( "farbwechsel:" + this.meinIndex + ":blau" );
                break;
                
            // Spiel beenden und alle Ressourcen wieder freigeben
            case Taste.A:
                this.client.beendeVerbindung();
                break;
        }
    }


    /**
     * Die beiden Spieler werden in einem Array verwaltet.                      <br />
     *
     * Fuegt einen Spieler zum Array hinzu und zeigt ihn im Spiele-Fenster an.
     *
     * @param   name    Name des Spielers
     * @param   index   Index des Spielers im Array
     * @param   x       x-Koordinate des Mittelpunkts des Spielers
     * @param   y       y-Koordinate des Mittelpunkts des Spielers
     */
    public void neuerSpieler( String name , int index , int x , int y )
    {
        this.figuren[index] = new Spielfigur( x , y , name );
        this.figuren[index].setzeFarbe( "rot" );
        super.wurzel.add( this.figuren[index] );
    }


    /**
     * Bewegt Die Grafik des Spielers mit dem entsprechenden Index um (dx|dy).      <br />
     *
     * Die Spielfigur wird nicht gleich beim Druecken einer Pfeiltaste bewegt.
     * Das Tastatur-Ereignis wird erst an den Server uebertragen.
     * Der Server sendet daraufhin an jeden Client einen entsprechenden "Befehl",
     * der dann dort eine Veraenderung des Spiel-Zustands hervorruft.
     *
     *
     * @param   index   Der Indes des Spielers, der bewegt werden soll
     * @param   dx      Distanz in x-Richtung der Bewegung (in Pixel)
     * @param   dy      Distanz in y-Richtung der Bewegung (in Pixel)
     */
    public void bewegen( int index , int dx, int dy )
    {
        this.figuren[index].bewegen( dx , dy );
    }


    /**
     * Methode zum wechseln der Farbe eines Spielers.                   <br />
     *
     * Die Tastatur-Ereignisse werden erst an den Server uebertragen.
     * Dieser sendet daraufhin an jeden Client einen "Befehl", der dann dort
     * zu einer Veraenderung des Spiel-Zustands hervorruft.
     *
     * @param   index   Der Index des Spielers. dessen Farbe gewchselt werden soll
     * @param   farbe   Die neue Farbe des Spielers (rot, gruen, weiss, ...)
     */
    public void farbwechsel( int index , String farbe )
    {
        this.figuren[index].setzeFarbe( farbe );
    }


    /**
     * Gibt den Namen des eigenen Spielers zurueck.
     *
     * @return  Der Name des Spielers, der mit diesem Client spielt
     */
    public String name()
    {
        return this.name;
    }


    /**
     * Setzt den Index des Spielers, der mit diesem Client spielt.              <br />
     *
     * Die beiden Spieler werden in einem Array verwaltet.
     * Dieser index kennzeichnet im Array den eigenen Spieler.
     *
     * @param   index   Entweder 0 oder 1
     */
    public void setzeMeinIndex(int index)
    {
        this.meinIndex = index;
    }
    
}