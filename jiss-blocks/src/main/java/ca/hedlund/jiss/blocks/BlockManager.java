/*
 * jiss-blocks
 * Copyright (C) 2015, Gregory Hedlund <ghedlund@mun.ca>
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
