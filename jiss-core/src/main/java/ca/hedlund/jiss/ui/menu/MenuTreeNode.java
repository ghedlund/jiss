package ca.hedlund.jiss.ui.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import ca.hedlund.dp.extensions.ExtensionSupport;
import ca.hedlund.dp.extensions.IExtendable;
import ca.hedlund.dp.visitor.Visitable;

/**
 * Menu tree node.
 */
public abstract class MenuTreeNode<T> implements Visitable<MenuTreeNode<?>>, IExtendable {
	
	/**
	 * Node name
	 */
	private final String nodeName;
	
	/**
	 * Parent node
	 */
	private MenuTreeNode parent;
	
	/**
	 * Menu item object
	 */
	private T value;
	
	private final ExtensionSupport extSupport = new ExtensionSupport(MenuTreeNode.class, this);
	
	MenuTreeNode(String name, T value) {
		this.nodeName = name;
		this.value = value;
	}

	@Override
	public Set<Class<?>> getExtensions() {
		return extSupport.getExtensions();
	}

	@Override
	public <S> S getExtension(Class<S> cap) {
		return extSupport.getExtension(cap);
	}

	@Override
	public <S> S putExtension(Class<S> cap, S impl) {
		return extSupport.putExtension(cap, impl);
	}

	@Override
	public <S> S removeExtension(Class<S> cap) {
		return extSupport.removeExtension(cap);
	}
	
}
