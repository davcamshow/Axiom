package SIMAR;

import java.lang.reflect.Method;
import javax.swing.JOptionPane;

public class UtilNavegador {
    
    public static void abrirURL(String url) {
        String nombreSO = System.getProperty("os.name");
        
        try {
            if (nombreSO.startsWith("Mac OS")) {
                Class<?> manager = Class.forName("com.apple.eio.FileManager");
                Method openURL = manager.getDeclaredMethod("openURL", new Class[] { String.class });
                openURL.invoke(null, new Object[] { url });
            } else if (nombreSO.startsWith("Windows")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            } else {
                String[] navegadores = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
                String navegador = null;
                for (int contador = 0; contador < navegadores.length && navegador == null; contador++) {
                    Process p = Runtime.getRuntime().exec(new String[] { "which", navegadores[contador] });
                    if (p.waitFor() == 0) {
                        navegador = navegadores[contador];
                    }
                }
                if (navegador == null) throw new Exception("No se encuentra navegador web");
                
                Runtime.getRuntime().exec(new String[] { navegador, url });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al intentar lanzar el navegador web:\n" + e.getLocalizedMessage());
        }
    }
}