import javax.print.PrintException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;

public class FentreListeDeCourse extends JFrame implements ActionListener, Printable {
	private JPanel panel;
	private JList theList;
	private JButton print;
	private JButton suppr;
	private JPanel panneauDroite;
	private DefaultListModel model;


	public FentreListeDeCourse(){
		super();
		//debug
		this.model = new DefaultListModel();

		this.panel =(JPanel)getContentPane();
		setTitle("La super liste de Francky");
		setSize(1280,720);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setLayout(new BorderLayout());
		this.theList = new JList(this.model);
		this.theList.setLayoutOrientation(JList.VERTICAL);
		this.theList.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
		this.panel.add(this.theList,BorderLayout.CENTER);

		this.panneauDroite = new JPanel();
		this.panneauDroite.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.panel.add(this.panneauDroite,BorderLayout.EAST);

		this.suppr = new JButton("Supprimer l'article sel.");
		this.print = new JButton("Imprimer");
		this.suppr.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.print.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		this.suppr.addActionListener(this);
		this.print.addActionListener(this);
		this.panneauDroite.add(this.suppr);
		this.panneauDroite.add(this.print);
	}


	public void addToList(String name){

		if(this.model.size() != 0){
			int i = 0;
			boolean find = false;
			do{
				String nameIn = (String) this.model.get(i);

				if(nameIn.substring(0,nameIn.indexOf("      ")).equals(name)){
					find = true;
				}
				i++;
			}while (i < this.model.size() && !find);
			//            System.out.println(find);
			if(find){
				int index = i -1;
				String nameIn = (String) this.model.get(index);
				String tmp = nameIn.substring(0,nameIn.indexOf("      "));
				int nb = Integer.parseInt(nameIn.substring(nameIn.lastIndexOf("X") + 1)) + 1;
				this.model.set(index,tmp + "      X" + nb);
			}else{
				this.model.addElement(name + "      X1");
			}
		}else{
			this.model.addElement(name + "      X1");
		}

	}

	public String[] couperString(String string) {
		String[] returnString = new String[1];
		
		string.replace("      X", "______X");
		//string.replace(" \u2610", "_\u2610");

		if (string.length() > 34) {
						
			returnString = new String[(string.length()/36) + 1];

			String[] stringSplitted = string.split(" ");

			String tmp = "";
			int cpt = 0;
			int lignes = 0;

			for (int i = 0; i < stringSplitted.length; i++) {
				cpt = tmp.length() + stringSplitted[i].length() + 1;
				
				if (cpt < 36) {					
					tmp = tmp + stringSplitted[i] + " ";

					if (stringSplitted.length - 1 == i) {
						tmp.replace("______X", "      X");
						returnString[lignes] = tmp;
					}
				} else {
					returnString[lignes] = tmp;
					lignes++;
					cpt = 0;
					tmp.replace("______X", "      X");
					tmp = stringSplitted[i] + " " ;
				}
			}


		} else {
			returnString[0] = string;
		}

		return returnString;

	}

	private void delListSelected(){
		int selectedIndex = this.theList.getSelectedIndex();
		//System.out.println(selectedIndex);
		if(selectedIndex != -1){
			model.remove(selectedIndex);
		}else{
			JOptionPane.showMessageDialog(this,"Impossible de supprimer, rien n'est sélectionné","Attention",JOptionPane.ERROR_MESSAGE);
		}
	}

	public void printButton() throws PrintException, IOException {
		if(this.model.size() != 0){
			PrinterJob job = PrinterJob.getPrinterJob();

			PageFormat pf = job.defaultPage();
			Paper paper = pf.getPaper();
			paper.setSize(8.5 * 72, 11 * 72);
			paper.setImageableArea(0, 0.5 * 72, 2.83 * 72, 10.5 * 72);
			pf.setPaper(paper);

			Book book = new Book();//java.awt.print.Book
			book.append(this, pf);
			job.setPageable(book);

			//            job.setPrintable(this);

			//System.out.println(this);

			boolean ok = job.printDialog();
			if (ok) {
				try {
					job.print();
				} catch (PrinterException ex) {
					/* The job did not successfully complete */
				}
			}
		}else{
			System.out.println("wallah y a rien a imprimer gros con");
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == this.suppr){
			delListSelected();
		}
		if(e.getSource() == this.print){
			try {
				printButton();
			} catch (PrintException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex > 0) {
			return NO_SUCH_PAGE;
		}
		// User (0,0) is typically outside the
		// imageable area, so we must translate
		// by the X and Y values in the PageFormat
		// to avoid clipping.
		Graphics2D g2d = (Graphics2D)graphics;
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		//ajout des infos present dans la liste de gauche
		int y =10;
		for(int i=0;i< this.model.size();i++){
			String nameLocal = (String) this.model.get(i) + " \u2610";
			
			String[] arraytest= couperString(nameLocal);

//			String[] arraytest = nameLocal.split("(?<=\\G.{34})");

			for (int j = 0; j < arraytest.length; j++) {
				g2d.drawString(arraytest[j], 0, y);
				y = y + 15;
			}

			y = y + 15;

		}
		// tell the caller that this page is part
		// of the printed document
		return PAGE_EXISTS;
	}
}
