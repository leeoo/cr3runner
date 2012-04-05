/*
 * Copyright (C) 2012 Victor Pyankov
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package kz.pvnhome.cr3runner;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;

import com.amazon.kindle.kindlet.AbstractKindlet;
import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KButton;
import com.amazon.kindle.kindlet.ui.KPanel;
import com.amazon.kindle.kindlet.ui.KTextArea;

/**
 * Created Jan 20, 2012 1:35:11 PM
 * @author Victor Pyankov
 */
public class CR3RunnerKindlet extends AbstractKindlet implements CommandExecuter {
   private static final String CONFIG_DIR = "/mnt/us/cr3runner";
   private static final String CONFIG_FILE = "commands.txt";

   private KindletContext ctx;
   private Container rootContainer;
   private KTextArea textArea;

   public void create(KindletContext context) {
      this.ctx = context;
   }

   public void start() {
      try {
         rootContainer = ctx.getRootContainer();

         textArea = new KTextArea("Waiting for command");
         rootContainer.add(textArea, BorderLayout.CENTER);

         CommandLoader loader = new CommandLoader(CONFIG_DIR, CONFIG_FILE);
         List commands = loader.load();

         KPanel btnPanel = new KPanel();
         btnPanel.setLayout(new GridLayout(commands.size(), 1));

         for (int i = 0; i < commands.size(); i++) {
            Command command = (Command) commands.get(i);
            KButton btn = new KButton(command.getDescription());
            command.setExecuter(this);
            btn.addKeyListener(command);
            btnPanel.add(btn);
         }

         rootContainer.add(btnPanel, BorderLayout.NORTH);

      } catch (Throwable t) {
         textArea.setText(t.getMessage());
         rootContainer.repaint();
      }
   }

   public void execute(Command command) {
      textArea.setText("command: " + command.getDescription());
      rootContainer.repaint();

      try {
         Runtime runtime = Runtime.getRuntime();
         textArea.setText("runtime");
         rootContainer.repaint();

         // Process p = 
         runtime.exec(new String[]{command.getPath() + File.separatorChar + command.getCommand(), command.getParam()}, null, new File(command.getPath()));

         /*
          * Убрал ожидание и всю проверку результата выполнения по причине подозрений на то,
          * что это приодит к перезагрузке оболочки kindle.
          * 
         p.waitFor();

         int exitValue = p.exitValue();

         StringBuffer sb = new StringBuffer();
         InputStream is = p.getInputStream();
         InputStreamReader isr = new InputStreamReader(is);
         BufferedReader br = new BufferedReader(isr);
         String line;
         while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
         }

         if (exitValue > 0) {
            sb.append("ERROR");
         } else {
            sb.append("OK");
         }

         textArea.setText(sb.toString());
         rootContainer.repaint();
         */

      } catch (Throwable ex1) {
         textArea.setText(ex1.getMessage());
         rootContainer.repaint();
      }
   }
}
