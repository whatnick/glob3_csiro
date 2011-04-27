

package es.igosoftware.graph;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import es.igosoftware.util.GAssert;


public class GGraph<NodeT> {

   private final Set<NodeT>             _nodes;
   private final Map<NodeT, Set<NodeT>> _neighborhood = new HashMap<NodeT, Set<NodeT>>();


   public GGraph(final Collection<NodeT> nodes) {
      GAssert.notEmpty(nodes, "nodes");

      _nodes = new HashSet<NodeT>(nodes);
   }


   public GGraph(final NodeT... nodes) {
      GAssert.notEmpty(nodes, "nodes");

      _nodes = new HashSet<NodeT>();
      for (final NodeT node : nodes) {
         _nodes.add(node);
      }
   }


   public void addBidirectionalEdge(final NodeT node1,
                                    final NodeT node2) {
      checkNodeExists(node1);
      checkNodeExists(node2);

      getOrCreateNeighbors(node1).add(node2);

      getOrCreateNeighbors(node2).add(node1);
   }


   protected Set<NodeT> getOrCreateNeighbors(final NodeT node) {
      Set<NodeT> neighbors = _neighborhood.get(node);
      if (neighbors == null) {
         neighbors = new HashSet<NodeT>();
         _neighborhood.put(node, neighbors);
      }
      return neighbors;
   }


   private void checkNodeExists(final NodeT node) {
      if (!_nodes.contains(node)) {
         throw new RuntimeException("Node " + node + " not present in the graph");
      }
   }


   @Override
   public String toString() {
      return "GGraph [edges=" + getEdgesCount() + ", nodes=" + _nodes.size() + "]";
   }


   public void printStructure(final PrintStream out) {
      for (final Entry<NodeT, Set<NodeT>> neighbors : _neighborhood.entrySet()) {
         for (final NodeT neighbor : neighbors.getValue()) {
            out.println(neighbors.getKey() + " -> " + neighbor);
         }
      }
   }


   public Set<NodeT> neighbors(final NodeT node) {
      final Set<NodeT> neighbors = _neighborhood.get(node);
      if (neighbors == null) {
         return Collections.emptySet();
      }
      return neighbors;
   }


   public int getNodesCount() {
      return _nodes.size();
   }


   public long getEdgesCount() {
      long count = 0;
      for (final Set<NodeT> neighbors : _neighborhood.values()) {
         count += neighbors.size();
      }
      return count;
   }


   public boolean isAdjacent(final NodeT node1,
                             final NodeT node2) {
      final Set<NodeT> neighbors = _neighborhood.get(node1);
      if (neighbors == null) {
         return false;
      }
      return neighbors.contains(node2);
   }


   public Collection<Set<NodeT>> getConnectedGroupsOfNodes() {
      final Collection<Set<NodeT>> result = new ArrayList<Set<NodeT>>();

      final LinkedList<NodeT> toProcess = new LinkedList<NodeT>(_nodes);

      while (!toProcess.isEmpty()) {
         final NodeT current = toProcess.removeFirst();
         final Set<NodeT> group = new HashSet<NodeT>();
         result.add(group);
         final IGraphVisitor<NodeT> visitor = new IGraphVisitor<NodeT>() {
            @Override
            public void visitNode(final NodeT node) {
               group.add(node);
               toProcess.remove(node);
            }
         };
         depthFirstAcceptVisitor(current, true, false, visitor);
      }

      return result;
   }


   public void depthFirstAcceptVisitor(final NodeT node,
                                       final boolean preVisit,
                                       final boolean postVisit,
                                       final IGraphVisitor<NodeT> visitor) {
      final Set<NodeT> visited = new HashSet<NodeT>();
      depthFirstAcceptVisitor(visited, node, preVisit, postVisit, visitor);
   }


   private void depthFirstAcceptVisitor(final Set<NodeT> visited,
                                        final NodeT node,
                                        final boolean preVisit,
                                        final boolean postVisit,
                                        final IGraphVisitor<NodeT> visitor) {
      visited.add(node);

      if (preVisit) {
         visitor.visitNode(node);
      }

      for (final NodeT neighbor : neighbors(node)) {
         if (!visited.contains(neighbor)) {
            depthFirstAcceptVisitor(visited, neighbor, preVisit, postVisit, visitor);
         }
      }

      if (postVisit) {
         visitor.visitNode(node);
      }
   }


   public static void main(final String[] args) {
      System.out.println("GGraph 0.1");
      System.out.println("----------\n");

      final GGraph<String> graph = new GGraph<String>("Apple", "Orange", "Lemon", "Watermelon", "A", "B", "C", "Alone");
      graph.addBidirectionalEdge("Apple", "Orange");
      graph.addBidirectionalEdge("Lemon", "Orange");
      graph.addBidirectionalEdge("Watermelon", "Orange");

      graph.addBidirectionalEdge("A", "B");
      graph.addBidirectionalEdge("A", "C");

      System.out.println(graph);

      System.out.println();
      graph.printStructure(System.out);
      System.out.println();


      graph.depthFirstAcceptVisitor("Apple", true, false, new IGraphVisitor<String>() {
         @Override
         public void visitNode(final String node) {
            System.out.println(node);
         }
      });

      System.out.println();

      final Collection<Set<String>> connectedGroups = graph.getConnectedGroupsOfNodes();
      System.out.println(connectedGroups);

   }


}
