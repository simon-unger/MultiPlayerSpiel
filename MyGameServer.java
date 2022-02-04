import ea.*;

/**
 * @Description     Server-Klasse eines Demo-Netzwerk-Spiels.
 *                  Bevor keine Server-Instanz erzeugt wurde, kann sich kein Spieler anmelden!
 *                  Der Server kann auf einem beliebigen Rechner (auch auf einem Client-Rechner) gestartet werden.
 *                  Anschliessend verbinden sich die beiden Clients mit dem Server.
 *                  Die Clients senden jede gewuenschte Zustands-Aenderung erst an den Server.
 *                  Dieser sendet daraufhin als Reaktion an beide Clients "Befehle" zur Aenderung
 *                  des lokalen Spiel-Zustands.
 *
 * @Author          Mike Ganshorn
 *
 * @Version         1.1 (2015-02-18)
 *
 */
class MyGameServer
        // Ein Server lauscht nur auf Verbindungs-Anfragen. Zum Antworten auf eine Anfrage hat er bereits 
        // das SenderInterface implementiert, welches ihm die Methode senden(...) verschafft.
        // Um auch auf weitere Sendungen von den Clients ( nach der Anmeldung) reagieren zu koennen, 
        // musst du noch das Interface Empfaenger implementieren, das die Methoden empfange...(...) verschafft
        implements ea.Empfaenger
{
    private String[] spieler;
    private int akt_spieler;
    private ea.Server server;


    /**
     * Konstruktor der Klasse MyGameServer.                         <br />
     *
     * Muss auf einem beliebigen Rechner gestartet werden, bevor sich Clients anmelden koennen.
     *
     * @param   port    Der Port auf dem der Server lauschen soll.
     */
    public MyGameServer( int port )
    {
        server = new ea.Server( port );
        this.spieler = new String[2];   // Bei unbekannter Client-Zahl ist eine java.util.ArrayList besser geeignet
        this.akt_spieler = 0;
        // Dieses MyServer-Objekt benachrichtigen, wenn der Server eine Nachricht empfaengt. 
        // Wegen Implementation des Interfaces ist unser MyGameServer auch Empfaenger fuer Netzwerk-Kommunikation
        server.globalenEmpfaengerSetzen( this );
    }


    /**
     * Vom Interface Empfaenger vorgeschriebene Methode zum Empfangen eines Strings von der Netzwerk-Gegenstelle.
     * Diese Methode wird auftomatisch aufgerufen, sobald von der Netzwerk-Gegenstelle ein String lokal ankommt.
     * <br />
     *
     * Hier wird ein sehr primitives Protokoll mit nur 3 "Befehlen" verwendet:
     * anmelden, bewegen, farbwechsel                                                         <br />
     *
     * Jeder Client sendet jeden Aenderungs-Wunsch erst an den Server und dieser sendet
     * danach als Reaktion an beide (alle) Clients den entsprechenden "Befehl" zur Aenderung
     * des lokalen Spiel-Zustands.
     *
     * @param   s   Der empfangene Text ("Befehl" vom Server - definiert im Protokoll)
     */
    @Override
    public void empfangeString( String s )
    {
        // Das Protokoll besteht aus folgender Logik:
        // Jeder Befehl besteht aus mehreren Teilen, die mit Doppelpunkt getrennt als ein String gesendet werden.
        
        // Empfangenen Befehl in seine Bestandteile auftennen - Doppelpunkte werden dabei entfernt
        String[] befehlBestandteile = s.split( ":" );
        
        // Die Fallunterscheidung realisiert die Umsetzung des Protokolls:
        
        // Struktur des 1. Befehls:  anmelden:name
        if ( befehlBestandteile[0].equals("anmelden") ) {
            // erst auf beide Spieler warten
            if ( this.akt_spieler < this.spieler.length ) {
                this.spieler[this.akt_spieler] = befehlBestandteile[1];
                this.akt_spieler++;
            }
            // danach bei den Clients das Spiel starten
            if ( this.akt_spieler == this.spieler.length ) {
                // Das Protokoll sieht folgende Struktur vor:   neu:name:index:x:y
                // Ein "Befehl" dieser Stuktur muss an die Clients gesendet werden
                int x;
                int y;
                // Zufaelligen Ort fuer Spieler 1 bestimmen
                x = (int)((Math.random()) * 700 + 50);
                y = (int)((Math.random()) * 500 + 50);
                server.sendeString("neu:" + this.spieler[0] + ":0:" + x + ":" + y);
                // Zufaelligen Ort fuer Spieler 2 bestimmen
                x = (int)((Math.random()) * 700 + 50);
                y = (int)((Math.random()) * 500 + 50);
                server.sendeString("neu:" + this.spieler[1] + ":1:" + x + ":" + y);
            }
        }
        
        // Struktur des 2. Befehls:  bewegen:index:dx:dy
        else if ( befehlBestandteile[0].equals("bewegen") ) {
            // Wunsch auf Zustands-Aenderung an beide (alle) Clients weiter leiten
            server.sendeString(s);
        }
        
        // farbwechsel:index:farbe
        else if ( befehlBestandteile[0].equals("farbwechsel") ) {
            // Wunsch auf Zustands-Aenderung an beide (alle) Clients weiter leiten
            server.sendeString(s);
        }
    }


    // Info: Diese Methoden werden vom Interface Empfaenger gefordert. Sie muessen implementiert werden.
    //       Ihre Ruempfe bleiben leer, da diese Methoden in der Demo nicht verwendet werden.

    @Override
    public void empfangeInt (int i)
    {
        // Diese Methode wird in dieser Demo nicht aufgerufen
    }
 
    @Override
    public void empfangeByte (byte b)
    {
        // Diese Methode wird in dieser Demo nicht aufgerufen
    }
 
    @Override
    public void empfangeDouble (double d)
    {
        // Diese Methode wird in dieser Demo nicht aufgerufen
    }
 
    @Override
    public void empfangeChar (char c)
    {
        // Diese Methode wird in dieser Demo nicht aufgerufen
    }
 
    @Override
    public void empfangeBoolean (boolean b)
    {
        // Diese Methode wird in dieser Demo nicht aufgerufen
    }
    
    @Override
    public void verbindungBeendet ()
    {
        // Diese Methode wird in dieser Demo nicht aufgerufen
    }
 
}