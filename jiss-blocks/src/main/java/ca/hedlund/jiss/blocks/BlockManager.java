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
package ca.hedlund.jiss.blocks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import ca.hedlund.jiss.blocks.preprocessor.ListBlocksPreprocessor;

/**
 * Helps save/load blocks from disk.
 */
public class BlockManager {

	/**
	 * Block root folder
	 */
	private final static String ROOT_FOLDER = 
			System.getProperty("user.home") + File.separator + 
			".jiss" + File.separator + "blocks";
	
	/**
	 * Constructor
	 */
	public BlockManager() {
		super();
		
	}
	
	/**
	 * Save block
	 * 
	 * @param path
	 * @param block
	 * 
	 * @throws IOException
	 */
	public void saveBlock(BlockPath path, Block block)
		throws IOException {
		final String relPath = path.getPath();
		final File blockFile = new File(ROOT_FOLDER, relPath);
		final File parentFolder = blockFile.getParentFile();
		if(!parentFolder.exists()) {
			parentFolder.mkdirs();
		}
		try {
			final JAXBContext context = JAXBContext.newInstance(Block.class);
			final Marshaller marshaller = context.createMarshaller();
			final JAXBElement<Block> ele = new JAXBElement<Block>(new QName("block"), Block.class, block);
			marshaller.marshal(ele, blockFile);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}
	
	/**
	 * Load block from the given path.
	 * 
	 * @param path
	 * @return block
	 * @throws IOException if the given path
	 *  is not found or the block could not be
	 *  loaded
	 */
	public Block loadBlock(BlockPath path)
		throws IOException {
		final String relPath = path.getPath();
		final File blockFile = new File(ROOT_FOLDER, relPath);
		if(blockFile.exists()) {
			try {
				final JAXBContext context = JAXBContext.newInstance(Block.class);
				final Unmarshaller unmarshaller = context.createUnmarshaller();
				final XMLInputFactory inputFactory = XMLInputFactory.newFactory();
				final XMLEventReader evtReader = 
						inputFactory.createXMLEventReader(new FileInputStream(blockFile));
				final JAXBElement<Block> ele = 
						unmarshaller.unmarshal(evtReader, Block.class);
				return ele.getValue();
			} catch (JAXBException e) {
				throw new IOException(e);
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
			
		} else {
			throw new FileNotFoundException(relPath);
		}
	}
	
	/**
	 * List blocks in the given path
	 * 
	 * @return list of block names
	 * @throws IOException
	 */
	public List<String> getBlocks()  {
		final File rootFolder = new File(ROOT_FOLDER);
		if(rootFolder.exists()) {
			return Arrays.asList(rootFolder.list());
		}
		return new ArrayList<String>();
	}
	
}
