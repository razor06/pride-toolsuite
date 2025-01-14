package uk.ac.ebi.pride.gui.component.metadata;

import uk.ac.ebi.pride.data.core.Parameter;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.component.SharedLabels;
import uk.ac.ebi.pride.gui.utils.HttpUtilities;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.Collection;

/**
 * User: rwang
 * Date: 25-May-2010
 * Time: 11:01:19
 */
public class MetaDataViewer extends JPanel {
    private final Collection<Parameter> params;

    public MetaDataViewer(Collection<Parameter> params) {
        this.params = params;
        setMainPane();
        addComponents();
    }

    private void setMainPane() {
        this.setLayout(new GridBagLayout());
    }

    private void addComponents() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.WEST;

        for (int i = 0; i < params.size(); i++) {
            Parameter param = CollectionUtils.getElement(params, i);
            String name = param.getName();
            String value = param.getValue();
            if (value == null || "".equals(value.trim())) {
                value = name;
                name = SharedLabels.PARAMETER;
            }
            // add name label
            JLabel label = new JLabel(name);
            c.gridx = 0;
            c.gridy = i;
            c.fill = GridBagConstraints.NONE;
            c.weightx = 0;
            this.add(label, c);
            // add value textfield
            JComponent textComp = null;
            if (value.contains("<html>")) {
                JTextPane textPane = new JTextPane();
                textPane.setContentType("text/html");
                textPane.setEditorKit(new HTMLEditorKit());
                textPane.setText(value);
                textPane.setEditable(false);
                textPane.addHyperlinkListener(new HyperLinkFollower());
                textComp = textPane;
            } else {
                JTextArea textArea = new JTextArea();
                textArea.setText(value);
                textArea.setEditable(false);
                textArea.setLineWrap(true);
                textComp = textArea;
            }
            textComp.setBackground(Color.white);

            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            this.add(textComp, c);
        }
    }

    public class HyperLinkFollower implements HyperlinkListener {

        public void hyperlinkUpdate(HyperlinkEvent evt) {

            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    HttpUtilities.openURL(evt.getURL().toString());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
