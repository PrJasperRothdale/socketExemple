// Figure 21.3: Serveur.java
// Mettre en place un serveur qui attend la connexion
// d'un client, envoie une cha�ne de caract�res au client,
// et ferme la connexion.
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {
   private JTextField texte;
   private JTextArea affichage;
   ObjectOutputStream sortie;
   ObjectInputStream entree;

   public Server()
   {
      super( "Server" );

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

   public void lancerServeur()
   {
      ServerSocket serveur;
      Socket connexion;
      int compteur = 1;

      try {
         // Etape 1: Cr�er un ServerSocket.
         serveur = new ServerSocket( 5432, 1 );

         while ( true ) {
            // Etape 2: Attendre une connexion.
            affichage.setText( "Waiting for connection\n" );
            connexion = serveur.accept();

            affichage.append( "Connexion " + compteur +
               " re�ue de: " +
               connexion.getInetAddress().getHostName() );

            // Etape 3: Get des flux d'entr�e et de sortie.
            sortie = new ObjectOutputStream(
                         connexion.getOutputStream() );
            sortie.flush();
            entree = new ObjectInputStream(
                        connexion.getInputStream() );
            affichage.append( "\nFlux d'E/S acquis\n" );

            // Etape 4: Proc�der � la connexion.
            String message =
               "SERVEUR>>> connexion r�ussie";
            sortie.writeObject( message );
            sortie.flush();
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
            } while ( !message.equals( "CLIENT>>> TERMINER" ) );

            // Etape 5: Close connexion.
            affichage.append( "\nUtilisateur a cl�tur� connexion" );
            texte.setEnabled( false );
            sortie.close();
            entree.close();
            connexion.close();

            ++compteur;
         }
      }
      catch ( EOFException eof ) {
         System.out.println( "Client a cl�tur� connexion" );
      }
      catch ( IOException io ) {
         io.printStackTrace();
      }
   }

   private void envoyerDonnees( String s )
   {
      try {
         sortie.writeObject( "SERVEUR>>> " + s );
         sortie.flush();
         affichage.append( "\nSERVEUR>>>" + s );
      }
      catch ( IOException cnfex ) {
         affichage.append(
            "\nErreur �criture de l'objet" );
      }
   }

   public static void main( String args[] )
   {
      Server app = new Server();

      app.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         }
      );

      app.lancerServeur();
   }
}