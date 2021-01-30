package functionfitting;

import java.io.File;

import chirpModels.UserChirpModel;



	public class Split implements Runnable  {

		final InteractiveChirpFit parent;
		final int fileindex;

		public Split(InteractiveChirpFit parent, final int fileindex) {

			this.parent = parent;
			this.fileindex = fileindex;
		}

		public void run() {
			
			parent.displaymuteclicked(fileindex);
			
			
		}
		
}
