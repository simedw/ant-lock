package com.simedw.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

public class Lock extends Task {
	private String lockName;
	private List<Task> elements = new ArrayList<Task>();
	private FileLock lock;
	private FileOutputStream fos;

	public String getName() {
		return lockName;
	}
	
	public void setName(String lockName) {
		this.lockName = lockName;
	}
	
	public void add(Task task) {
		elements.add(task);
	}
	
	@Override
	public void execute() {
		if(getName() == null) {
			throw new BuildException("property \"name\" is not set");
		}
		
		log("trying to aquiring lock \"" + getName() + "\"");
		
		createLock();
		lock();
		log("aquired \"" + getName() + "\"");
		for(Task task : elements) {
			task.execute();
		}
		release();
		log("released \"" + getName() + "\"");
	}

	/**
	 * Creates file for locking
	 */
	private void createLock() {
		File lockFile = new File("lock-" + getName());
		// First we need to create the file to lock on.
		try {
			lockFile.createNewFile();
			fos = new FileOutputStream(lockFile);
		} catch (IOException e) {
			log("creating lock failed", Project.MSG_ERR);
			throw new BuildException(e);
		}
	}
	
	/**
	 * Waits indefinitely for lock
	 */
	private void lock() {
		try {
			lock = fos.getChannel().lock();
		} catch (IOException e) {
			log("aquiring lock failed", Project.MSG_ERR);
			throw new BuildException(e);
		}
	}
	
	/**
	 * Releases current lock
	 */
	private void release() {
		try {
			lock.release();
			fos.close();
		} catch (IOException e) {
			log("releasing lock failed", Project.MSG_ERR);
			throw new BuildException(e);
		}
	}
}
