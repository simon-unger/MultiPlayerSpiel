import ea.*;

/**
 * @Description     Netzwerk-Client im Demo-Netzwerk-Spiel.
 *                  Hiervon ist eine Instanz in der Klasse MyGame verantwortlich
 *                  fuer die eigentliche Netzwerk-Kommunikation mit dem Server.
 */
public class MyNetworkClient
        extends ea.Client
{
    // Damit der CLient 'weiss', zu welcher der beiden Spielfiguren er gehoert 
    private int meinIndex;
    // Referenz auf das GrfikFenster des entsprechenden CLients, um dort Aenderungen zu bewirken
    private MyGame spiel;


    /**
     * Konstruktor der Klasse MyNetworkClient.
     *
     * @param   ip      Die IP-Adresse des Servers
     * @param   port    Der Port auf dem der Server lauscht
     */
    public MyNetworkClient( String ip , int port )
    {
        super( ip,port );
        this.meinIndex = -1;
    }


    /**
     * Von der Klasse Client vorgeschriebene Methode zum Empfangen eines Strings von der Netzwerk-Gegenstelle.
     * Diese Methode wird auftomatisch aufgerufen, sobald von der Netzwerk-Gegenstelle ein String lokal ankommt.
     * <br />
     *
     * Hier wird ein sehr primitives Protokoll mit nur 3 "Befehlen" verwendet:
     * neu, bewegen, farbwechsel                                                         <br />
     *
     * Jeder Client sendet jeden Aenderungs-Wunsch erst an den Server und dieser sendet
     * danach als Reaktion an beide Clients den entsprechenden "Befehl" zur Aenderung
     * des lokalen Spiel-Zustands.
     *
     * @param   s   Der empfangene Text ("Befehl" aus dem Protokoll)
     */
    @Override
    public void empfangeString( String s )
    {
        String[] befehle = s. split( ":" );
        
        // Struktur des 1. Befehls:     neu:name:index:x:y
        if ( befehle[0].equals("neu") ) {
            this.spiel.neuerSpieler( befehle[1] , Integer.parseInt(befehle[2]) , Integer.parseInt(befehle[3]) , Integer.parseInt(befehle[4]) );
            // Erst nach der ersten Server-Antwort kann der Index des Spielers festgelgt werden
            if ( befehle[1].equals( this.spiel.name() ) )  {
                this.meinIndex = Integer.parseInt( befehle[2] );
                this.spiel.setzeMeinIndex( this.meinIndex );
            }
        }
        
        // Struktur des 1. Befehls:     bewegen:index:dx:dy
        else if ( befehle[0].equals("bewegen") ) {
            this.spiel.bewegen( Integer.parseInt(befehle[1]) , Integer.parseInt(befehle[2]) , Integer.parseInt(befehle[3]) );
        }
        
        // Struktur des 1. Befehls:     farbwechsel:index:farbe
        else if ( befehle[0].equals("farbwechsel") ) {
            this.spiel.farbwechsel( Integer.parseInt(befehle[1]) , befehle[2] );
        }
        
    }


    /**
     * Hiermit bekommt der MyNetworkClient eine Referenz auf das MyGame-Objekt, das ihn erzeugt hat.
     * Damit koennen die vom Server erhaltenen "Befehle" an das lokal Spiel-steuernde MyGame-Objekt
     * weiter geleitet werden.
     *
     * @param   spiel   Referenz auf das MyGame-Objekt
     */
    public void setzeSpiel( MyGame spiel )
    {
        this.spiel = spiel;
    }


    /**
     * Nennt den Index des zugehoerigen Spielers, der vorher mit dem Server ausgehandelt wurde.     <br />
     *
     * Die beiden Spieler werden bei den MyGame-Objekten lokal in einem Array (oder einer java.util.ArrayList) verwaltet.
     * Dieser Index beschreibt, unter welchem Index der Spieler bei seinem MyGame-Objekt verwaltet wird.
     *
     * @return      Der Index, unter dem der Spieler lokal verwaltet wird. (0 oder 1)
     */
    public int nenneMeinIndex()
    {
        return this.meinIndex;
    }
}