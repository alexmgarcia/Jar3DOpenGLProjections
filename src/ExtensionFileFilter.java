import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileFilter;

/**
 * @author Alexandre Martins Garcia, 34625 G23 P5
 *
 * This class represents the Extension File Filter to use when selecting a .x24 file
 */
public class ExtensionFileFilter extends FileFilter {
	
	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		Pattern p = Pattern.compile(".*(\\.x24|\\.X24)$");
		Matcher m = p.matcher(f.getName());
		return (m.find());
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return ".x24 image files";
	}

}
