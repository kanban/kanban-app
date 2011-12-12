package com.metservice.kanban.model;

import java.util.Iterator;

public class TreeIterator<T> implements Iterator<T> {
    
    private TraversalPosition<T> position;

    public TreeIterator(TreeNode<T> root) {
        this.position = new TraversalPosition<T>(null, root);
    }

    @Override
    public boolean hasNext() {
        return position != null;
    }

    @Override
    public T next() {
        T result = position.getNode().getValue();
        position = position.advance();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    private static class TraversalPosition<T> {
        private static final int NODE_ITSELF = -1;
        
        private final TraversalPosition<T> parent;
        private final TreeNode<T> node;
        private int branchIndex;
        
        public TraversalPosition(TraversalPosition<T> parent, TreeNode<T> node) {
            this.parent = parent;
            this.node = node;
            this.branchIndex = NODE_ITSELF;
        }

        public TraversalPosition<T> advance() {
            int nextIndex = branchIndex + 1;
            if (nextIndex < node.getNumberOfChildren()) {
                branchIndex = nextIndex;
                return new TraversalPosition<T>(this, node.getChild(branchIndex));
            } 
            return parent == null ? null : parent.advance();
        }
        
        public TreeNode<T> getNode() {
            return node;
        }
    }
}
