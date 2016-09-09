/*
 *                    BioJava development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the BioJava project and its aims,
 * or to join the biojava-l mailing list, visit the home page
 * at:
 *
 *      http://www.biojava.org/
 *
 * Created on Oct 6, 2009
 * Author: Andreas Prlic
 *
 */

package ultiJmol1462;


import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolStatusListener;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;


public class JmolPanel
extends JPrintPanel
implements ActionListener
{
	private static final long serialVersionUID = -3661941083797644242L;

	private JmolViewer viewer;
	private JmolAdapter adapter;
	JmolStatusListener statusListener;
	final Dimension currentSize = new Dimension();
	final Rectangle rectClip = new Rectangle();

	private boolean verbose = false;

	public JmolPanel() {
		super();
		statusListener = new MyJmolStatusListener();
		adapter = new SmarterJmolAdapter();
		Logger.setLogLevel( verbose?Logger.LEVEL_INFO:Logger.LEVEL_ERROR);
		viewer = JmolViewer.allocateViewer(this,
				adapter,
				null,null,null,null,
				statusListener);

	}

	@Override
	public void paint(Graphics g) {

		getSize(currentSize);
		g.getClipBounds(rectClip);
		viewer.renderScreenImage(g, currentSize, rectClip);
	}

	public void evalString(String rasmolScript){

		viewer.evalString(rasmolScript);

	}

	public void openStringInline(String pdbFile){
		viewer.openStringInline(pdbFile);

	}
	public JmolViewer getViewer() {
		return viewer;
	}

	public JmolAdapter getAdapter(){
		return adapter;
	}

	public JmolStatusListener getStatusListener(){
		return statusListener;
	}
	public void executeCmd(String rasmolScript) {
		viewer.evalString(rasmolScript);
	}


	/** assign a custom color to the Jmol chains command.
	 *
	 */
	public void jmolColorByChain(){
		String script =
				"function color_by_chain(objtype, color_list) {"+ String.format("%n") +
				""+ String.format("%n") +
				"		 if (color_list) {"+ String.format("%n") +
				"		   if (color_list.type == \"string\") {"+ String.format("%n") +
				"		     color_list = color_list.split(\",\").trim();"+ String.format("%n") +
				"		   }"+ String.format("%n") +
				"		 } else {"+ String.format("%n") +
				"		   color_list = [\"104BA9\",\"AA00A2\",\"C9F600\",\"FFA200\",\"284A7E\",\"7F207B\",\"9FB82E\",\"BF8B30\",\"052D6E\",\"6E0069\",\"83A000\",\"A66A00\",\"447BD4\",\"D435CD\",\"D8FA3F\",\"FFBA40\",\"6A93D4\",\"D460CF\",\"E1FA71\",\"FFCC73\"];"+ String.format("%n") +
				"		 }"+ String.format("%n") +

				"		 var cmd2 = \"\";"+ String.format("%n") +

				"		 if (!objtype) {"+ String.format("%n") +
				"		   var type_list  = [ \"backbone\",\"cartoon\",\"dots\",\"halo\",\"label\",\"meshribbon\",\"polyhedra\",\"rocket\",\"star\",\"strand\",\"strut\",\"trace\"];"+ String.format("%n") +
				"		   cmd2 = \"color \" + type_list.join(\" none; color \") + \" none;\";"+ String.format("%n") +
				"		   objtype = \"atoms\";"+ String.format("%n") +

				"		 }"+ String.format("%n") +

				"		 var chain_list  = script(\"show chain\").trim().lines;"+ String.format("%n") +
				"		 var chain_count = chain_list.length;"+ String.format("%n") +

				"		 var color_count = color_list.length;"+ String.format("%n") +
				"		 var sel = {selected};"+ String.format("%n") +
				"		 var cmds = \"\";"+ String.format("%n") +


				"		 for (var chain_number=1; chain_number<=chain_count; chain_number++) {"+ String.format("%n") +
				"		   // remember, Jmol arrays start with 1, but % can return 0"+ String.format("%n") +
				"		   cmds += \"select sel and :\" + chain_list[chain_number] + \";color \" + objtype + \" [x\" + color_list[(chain_number-1) % color_count + 1] + \"];\" + cmd2;"+ String.format("%n") +
				"		 }"+ String.format("%n") +
				"		 script INLINE @{cmds + \"select sel\"}"+ String.format("%n") +
				"}";

		executeCmd(script);
	}

	/** The user selected one of the Combo boxes...
	 *
	 * @param event an ActionEvent
	 */
	@Override
	public void actionPerformed(ActionEvent event) {

		Object mysource = event.getSource();

		if ( ! (mysource instanceof JComboBox )) {
			super.actionPerformed(event);
			return;
		}

		JComboBox source = (JComboBox) event.getSource();
		String value = source.getSelectedItem().toString();
		evalString("save selection; ");

		String selectLigand = "select ligand;wireframe 0.16;spacefill 0.5; color cpk ;";

		if ( value.equals("Cartoon")){
			String script = "hide null; select all;  spacefill off; wireframe off; backbone off;" +
					" cartoon on; " +
					" select ligand; wireframe 0.16;spacefill 0.5; color cpk; " +
					" select *.FE; spacefill 0.7; color cpk ; " +
					" select *.CU; spacefill 0.7; color cpk ; " +
					" select *.ZN; spacefill 0.7; color cpk ; " +
					" select all; ";
			this.executeCmd(script);
		} else if (value.equals("Backbone")){
			String script = "hide null; select all; spacefill off; wireframe off; backbone 0.4;" +
					" cartoon off; " +
					" select ligand; wireframe 0.16;spacefill 0.5; color cpk; " +
					" select *.FE; spacefill 0.7; color cpk ; " +
					" select *.CU; spacefill 0.7; color cpk ; " +
					" select *.ZN; spacefill 0.7; color cpk ; " +
					" select all; ";
			this.executeCmd(script);
		} else if (value.equals("CPK")){
			String script = "hide null; select all; spacefill off; wireframe off; backbone off;" +
					" cartoon off; cpk on;" +
					" select ligand; wireframe 0.16;spacefill 0.5; color cpk; " +
					" select *.FE; spacefill 0.7; color cpk ; " +
					" select *.CU; spacefill 0.7; color cpk ; " +
					" select *.ZN; spacefill 0.7; color cpk ; " +
					" select all; ";
			this.executeCmd(script);

		} else if (value.equals("Ligands")){
			this.executeCmd("restrict ligand; cartoon off; wireframe on;  display selected;");
		} else if (value.equals("Ligands and Pocket")){
			this.executeCmd(" select within (6.0,ligand); cartoon off; wireframe on; backbone off; display selected; ");
		} else if ( value.equals("Ball and Stick")){
			String script = "hide null; restrict not water;  wireframe 0.2; spacefill 25%;" +
					" cartoon off; backbone off; " +
					" select ligand; wireframe 0.16; spacefill 0.5; color cpk; " +
					" select *.FE; spacefill 0.7; color cpk ; " +
					" select *.CU; spacefill 0.7; color cpk ; " +
					" select *.ZN; spacefill 0.7; color cpk ; " +
					" select all; ";
			this.executeCmd(script);
		} else if ( value.equals("By Chain")){
			jmolColorByChain();
			String script = "hide null; select all;set defaultColors Jmol; color_by_chain(\"cartoon\"); color_by_chain(\"\"); " + selectLigand + "; select all; ";
			this.executeCmd(script);
		} else if ( value.equals("Rainbow")) {
			this.executeCmd("hide null; select all; set defaultColors Jmol; color group; color cartoon group; " + selectLigand + "; select all; " );
		} else if ( value.equals("Secondary Structure")){
			this.executeCmd("hide null; select all; set defaultColors Jmol; color structure; color cartoon structure;" + selectLigand + "; select all; " );

		} else if ( value.equals("By Element")){
			this.executeCmd("hide null; select all; set defaultColors Jmol; color cpk; color cartoon cpk; " + selectLigand + "; select all; ");
		} else if ( value.equals("By Amino Acid")){
			this.executeCmd("hide null; select all; set defaultColors Jmol; color amino; color cartoon amino; " + selectLigand + "; select all; " );
		} else if ( value.equals("Hydrophobicity") ){
			this.executeCmd("hide null; set defaultColors Jmol; select hydrophobic; color red; color cartoon red; select not hydrophobic ; color blue ; color cartoon blue; "+ selectLigand+"; select all; ");
		}
		evalString("restore selection; ");
	}







	/** Clean up this instance for garbage collection, to avoid memory leaks...
	 *
	 */
	public void destroy(){

		executeCmd("zap;");

		viewer = null;
		adapter = null;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
		if(statusListener instanceof MyJmolStatusListener) {
			((MyJmolStatusListener)statusListener).setVerbose(verbose);
		}
	}

}
