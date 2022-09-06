/*
 * Copyright (C) 2012-2018 Gregory Hedlund
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.hedlund.jiss.history;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import jakarta.xml.bind.*;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.XMLReader;

import ca.hedlund.dp.extensions.Extension;
import ca.hedlund.dp.extensions.ExtensionProvider;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.jiss.JissModel;
import ca.hedlund.jiss.ui.JissConsole;

/**
 * Class that deals with loading/saving history.
 *
 */
@Extension(JissModel.class)
public class JissHistoryManager implements ExtensionProvider {

	private final static Logger LOGGER =
			Logger.getLogger(JissHistory.class.getName());
	
	/**
	 * History file location property
	 */
	public final static String HISTORY_PATH_PROP = JissHistory.class.getName() + ".jiss_history";
	
	/**
	 * Default history file path
	 */
	public final static String DEFAULT_HISTORY_PATH = System.getProperty("user.home") + File.separator +
			".jiss" + File.separator + "history.xml";
	
	/**
	 * Return the history file.
	 * @return the history file
	 */
	public File getHistoryFile() {
		File historyFile = null;
		
		// TODO allow setting this variable using a pref/setting
		final String historyFilePath = DEFAULT_HISTORY_PATH;
		historyFile = new File(historyFilePath);
		
		final File historyParent = historyFile.getParentFile();
		if(!historyParent.exists()) {
			historyParent.mkdirs();
		}
		
		if(!historyFile.exists()) {
			try {
				historyFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.severe(e.getMessage());
			}
		}
		
		return historyFile;
	}
	
	/**
	 * Load the history file
	 * 
	 * @return this loaded history
	 */
	public synchronized JissHistory loadHistory()
		throws IOException {
		JissHistory retVal = new JissHistory();
		
		try {
			final JAXBContext context = JAXBContext.newInstance(JissHistory.class);
			final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
			final XMLEventReader xmlEvtReader = 
					xmlInputFactory.createXMLEventReader(new FileInputStream(getHistoryFile()));
			final Unmarshaller unmarshaller = context.createUnmarshaller();
			final JAXBElement<JissHistory> ele = unmarshaller.unmarshal(xmlEvtReader, JissHistory.class);
			
			retVal = ele.getValue();		
		} catch (JAXBException e) {
			throw new IOException(e);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
		
		return retVal;
	}
	
	public synchronized void saveHistory(JissHistory history) 
		throws IOException {
		try {
			final JAXBContext context = JAXBContext.newInstance(JissHistory.class);
			final Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			final JAXBElement<JissHistory> ele = 
					new JAXBElement<JissHistory>(new QName("history"), JissHistory.class, history);
			final XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newFactory();
			final XMLEventWriter xmlEvtWriter = xmlOutputFactory.createXMLEventWriter(new FileOutputStream(getHistoryFile()));
			marshaller.marshal(ele, xmlEvtWriter);
		} catch (JAXBException e) {
			throw new IOException(e);
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
		
	}
	
	@Override
	public void installExtension(IExtendable obj) {
		final JissModel model = JissModel.class.cast(obj);
		JissHistory history = new JissHistory();
		try {
			history = loadHistory();
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.putExtension(JissHistory.class, history);
	}
	
}
