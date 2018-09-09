
// Figure 21.4: Client.java
// D�finit un Client qui lira les informations envoy�es
// � partir d'un Serveur et affiche les informations.

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {
   private JTextField texte;
   private JTextArea affichage;
   ObjectOutputStream sortie;
   ObjectInputStream entree;
   String message = "";

   public Client()
   {
      super( "Client" );

      Container c = getContentPane();

      texte = new JTextField();
      texte.setEnabled( false );
      texte.addActionListener(
         new ActionListener() {
            public void actionPerformed( ActionEvent e )
            {
               envoyerDonnees( e.getActionCommand() );
            }
         }
      );
      c.add( texte, BorderLayout.NORTH );

      affichage = new JTextArea();
      c.add( new JScrollPane( affichage ),
             BorderLayout.CENTER );

      setSize( 300, 150 );
      show();
   }

   public void lanceClient()
   {
      Socket client;

      try {
         // Etape 1: Cr�er un Socket pour assurer la connexion.
         affichage.setText( "Tentative de connexion\n" );
         client = new Socket(
            InetAddress.getByName( "127.0.0.1" ), 5432 );

         affichage.append( "Connect� �: " +
            client.getInetAddress().getHostName() );

         // Etape 2: Get des flux d'entr�e et de sortie.
         sortie = new ObjectOutputStream(
                      client.getOutputStream() );
         sortie.flush();
         entree = new ObjectInputStream(
                     client.getInputStream() );
         affichage.append( "\nFlux d'E/S acquis\n" );

         // Etape 3: Proc�der � la connexion.
         texte.setEnabled( true );

         do {
            try {
               message = (String) entree.readObject();
               affichage.append( "\n" + message );
               affichage.setCaretPosition(
                  affichage.getText().length() );
            }
            catch ( ClassNotFoundException cnfex ) {
               affichage.append(
                  "\nType d'objet re�u inconnu" );
            }
         } while ( !message.equals( "SERVEUR>>> TERMINER" ) );

         // Etape 4: Clore la connexion.
         affichage.append( "Cl�ture de la connexion.\n" );
         entree.close();
         sortie.close();
         client.close();
      }
      catch ( EOFException eof ) {
         System.out.println( "Serveur a cl�tur� la connexion" );
      }
      catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   private void envoyerDonnees( String s )
   {
      try {
         message = s;
         sortie.writeObject( "CLIENT>>> " + s );
         sortie.flush();
         affichage.append( "\nCLIENT>>>" + s );
      }

      catch ( IOException cnfex ) {
         affichage.append(
            "\nErreur �criture de l'objet" );
      }
   }

   public static void main( String args[] )
   {
      Client app = new Client();

      app.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         }
      );

      app.lanceClient();
   }
}
