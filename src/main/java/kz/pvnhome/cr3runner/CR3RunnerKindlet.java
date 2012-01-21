package kz.pvnhome.cr3runner;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KTextArea;

public class CR3RunnerKindlet extends AbstractKindlet {
   private static final String LINUX_DIR = "/mnt/us/cr3";
   private static final String LINUX_CMD = "goqt.sh";
   private static final String PARAM = "cr3";

   private KindletContext ctx;
   private Container rootContainer;
   private KTextArea textArea;

   public void create(KindletContext context) {
      this.ctx = context;
   }

   public void start() {
      try {
         rootContainer = ctx.getRootContainer();

         KButton runBtn = new KButton("RUN");
         rootContainer.add(runBtn, BorderLayout.NORTH);

         runBtn.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
               //textArea.setText("Pressed " + e.getKeyCode());
               //rootContainer.repaint();
            }

            public void keyReleased(KeyEvent e) {
               textArea.setText("Released " + e.getKeyCode());
               rootContainer.repaint();

               try {
                  Runtime runtime = Runtime.getRuntime();
                  textArea.setText("runtime");
                  rootContainer.repaint();

                  Process p = runtime.exec(new String[]{LINUX_DIR + File.separatorChar + LINUX_CMD, PARAM}, null, new File(LINUX_DIR));

                  textArea.setText("proc started for path " + LINUX_DIR + " and command " + LINUX_CMD + ". Running command...");
                  rootContainer.repaint();
                  p.waitFor();

                  int exitValue = p.exitValue();

                  if (exitValue > 0) {
                     InputStream is = p.getInputStream();
                     InputStreamReader isr = new InputStreamReader(is);
                     BufferedReader br = new BufferedReader(isr);
                     String line;
                     StringBuilder sb = new StringBuilder();
                     while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                     }
                     textArea.setText(sb.toString());
                     rootContainer.repaint();
                  } else {
                     textArea.setText("OK");
                     rootContainer.repaint();
                  }
               } catch (Throwable ex1) {
                  textArea.setText(ex1.getMessage());
                  rootContainer.repaint();
               }
            }

            public void keyTyped(KeyEvent e) {
               //textArea.setText("Typed " + e.getKeyCode());
               //rootContainer.repaint();
            }
         });

         textArea = new KTextArea("Waiting for command");
         rootContainer.add(textArea, BorderLayout.CENTER);

      } catch (Throwable t) {
         t.printStackTrace();
      }
   }
}
