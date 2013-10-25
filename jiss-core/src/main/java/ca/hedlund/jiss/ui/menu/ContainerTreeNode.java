package ca.hedlund.jiss.ui.menu;

import java.util.LinkedList;
import java.util.List;

import ca.hedlund.dp.visitor.Visitor;

public final class ContainerTreeNode extends MenuTreeNode<String> {
	
	/**
	 * Children
	 */
	private List<MenuTreeNode<?>> children = 
			new LinkedList<MenuTreeNode<?>>();
	
	public ContainerTreeNode(String name, String title) {
		super(name, title);
	}

	@Override
	public void accept(Visitor<MenuTreeNode<?>> visitor) {
		visitor.visit(this);
		
	}

}
