package GalioBot;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;
import org.apache.commons.io.FileUtils;


public class Delete extends TimerTask {
	
	public Delete() {
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		cleanup();
		System.out.println("Files Cleaned");
	}
	
	private void cleanup() {
		File dr = new File(Download.downloadloc);
		//System.out.println(dr.getAbsolutePath());
		try {
			FileUtils.cleanDirectory(dr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}