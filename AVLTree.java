
/**
 *
 * AVLTree
 *
 * An implementation of an AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {

	private IAVLNode root;
	private IAVLNode min_node;
	private IAVLNode max_node;
	private final AVLNode virtual_node = new AVLNode(); //Create a constant virtual node for the AVL tree

	public AVLTree(){
		this.root = null;
	}

	/**
	 * public AVLTree(IAVLNode node)
	 *
	 * Constructor for an AVLTree with a given node as root (creates a subtree as an AVLTree object).
	 *
	 * Time complexity O(log h), when h is the height of node
	 */
	public AVLTree(IAVLNode node){
		this.root = node;
		node.setParent(null);
		this.min_node = this.getMin(node);
		this.max_node = this.getMax(node);
	}

  /**
   * public boolean empty()
   *
   * Returns true if and only if the tree is empty.
   *
   * Time complexity O(1)
   */
  public boolean empty() {
    return this.root == null;
  }

 /**
   * public String search(int k)
   *
   * Returns the info of an item with key k if it exists in the tree.
   * otherwise, returns null.
   * Calls get_position if the tree is not empty
   * Time complexity (includes get_position complexity): O(log(n))
   */
  public String search(int k){
	  if (empty()){ // check if the AVL tree is empty
		  return null;
	  }
	  IAVLNode found_node = getPosition(this.root, k);
	  if (found_node.getKey() == k){
		  return found_node.getValue();
	  }
	  return null;
  }

	/**
	 * public AVLNode getPosition(AVLNode node, int k)
	 *
	 * Returns the AVLNode with key k if found in the tree,
	 * otherwise returns the parent node of a node with key=k if it would be inserted
	 *
	 * @param node current subtree's root
	 * @param k given key
	 * @return  node if k == node.key
	 * 			recursive calls for left subtree and right subtree
	 * 			parent if node is a virtual node
	 * Time complexity O(log(n))
	 */

  public IAVLNode getPosition(IAVLNode node, int k){
	  if (node == null) {
		  return null;
	  }
	  if (node.getKey() == k){
		  return node;
	  }
	  else if (node.getKey() > k){
		  if (node.getLeft().getKey() == -1){
			  return node;
		  }
		  return getPosition(node.getLeft(), k);
	  }
	  else {// node.key < k
		  if (node.getRight().getKey() == -1){
			  return node;
		  }
		  return getPosition(node.getRight(), k);
	  }
	}

  /**
   * public int insert(int k, String i)
   *
   * Inserts an item with key k and info i to the AVL tree.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k already exists in the tree.
   * Time complexity O(log(n))
   */

   public int insert(int k, String i) {
	   if (this.empty()){
		   this.root = new AVLNode(k, i, null, this.virtual_node);
		   this.max_node = this.getRoot();
		   this.min_node = this.getRoot();
		   return 0;
	   }
	  IAVLNode parent_position = getPosition(this.getRoot(), k);
	  if (parent_position.getKey() == k){
		  return -1;
	  }
	  IAVLNode new_node = new AVLNode(k, i, (AVLNode) parent_position, this.virtual_node);
	  insertUpdate(new_node);
	  return insertRebalance(parent_position);
   }

	/**
	 * Updating the node's parent after a node was inserted and updating the min and max pointers of the tree
	 * @param node inserted node
	 * Time complexity O(1)
	 */
	public void insertUpdate(IAVLNode node){
		IAVLNode parent = node.getParent();
		if (node.getKey() < parent.getKey()){
			parent.setLeft(node);
		}
		else {
			parent.setRight(node);
		}
		if (node.getKey() < this.min_node.getKey()){
			this.min_node = node;
		}
		if (node.getKey() > this.max_node.getKey()){
			this.max_node = node;
		}
	}

	/**
	 * Rebalance the tree after a node was inserted
	 * @param node inserted node's parent
	 * @return # of rebalance operation
	 * Time complexity O(log(n))
	 */

	public int insertRebalance(IAVLNode node){
	   int cnt = 0;
	   while (node != null){
		   int bf = calculateBalanceFactor(node);
		   node.setSize();
		   int new_height = Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1;
		   boolean height_changed = (node.getHeight() != new_height);
		   if (Math.abs(bf) < 2) {
			   if (!height_changed) {
				   while (node != null){
					   node.setSize();
					   node = node.getParent();
				   }
				   return cnt;
			   }
			   node.setHeight(node.getHeight() + 1);
			   cnt++;
		   }
		   if (bf == 2){
			   if (calculateBalanceFactor(node.getLeft()) == 0){
				   node.getLeft().setHeight(node.getLeft().getHeight() + 1);
				   cnt += rightRotation(node);
				   cnt++;
			   }
			   else if (calculateBalanceFactor(node.getLeft()) == 1){
				   cnt += rightRotation(node);
				   node.setHeight(node.getHeight() - 1);
				   cnt++;
			   }
			   else if (calculateBalanceFactor(node.getLeft()) == -1){
				   IAVLNode left = node.getLeft();
				   left.setHeight(left.getHeight() - 1);
				   node.setHeight(node.getHeight() - 1);
				   left.getRight().setHeight(left.getRight().getHeight() + 1);
				   cnt += 3;
				   cnt += leftRotation(left);
				   cnt += rightRotation(node);
			   }
		   }
		   else if (bf == -2){
			   if (calculateBalanceFactor(node.getRight()) == 0){
				   node.getRight().setHeight(node.getRight().getHeight() + 1);
				   cnt += leftRotation(node);
				   cnt++;
			   }
			   else if (calculateBalanceFactor(node.getRight()) == -1){
				   cnt += leftRotation(node);
				   node.setHeight(node.getHeight() - 1);
				   cnt++;
			   }
			   else if (calculateBalanceFactor(node.getRight()) == 1){
				   IAVLNode right = node.getRight();
				   right.setHeight(right.getHeight() - 1);
				   node.setHeight(node.getHeight() - 1);
				   right.getLeft().setHeight(right.getLeft().getHeight() + 1);
				   cnt += 3;
				   cnt += rightRotation(right);
				   cnt += leftRotation(node);
			   }
		   }
		   node = node.getParent();
	   }
	   return cnt;
	}

	/**
	 * Calculating the balance factor of node
	 * @return 1: value of balance factor
	 * Time complexity O(1)
	 */
	public int calculateBalanceFactor(IAVLNode node){
		return node.getLeft().getHeight() - node.getRight().getHeight();
	}

	/**
	 * Rotating the edge between node and node's left child
	 * @param node upper vertical of the rotated edge
	 * @return 1: # of rebalance operations
	 * Time complexity O(1)
	 */

	public int rightRotation(IAVLNode node){
		IAVLNode left = node.getLeft();
	   // modify parent
	   if (node.getParent() == null) {
		   this.root = left;
		   left.setParent(null);
	   }
	   else if (node.getKey() < node.getParent().getKey()) { // node is left child
		   node.getParent().setLeft(left);
		   left.setParent(node.getParent());
	   }
	   else { // node is a right child
		   node.getParent().setRight(left);
		   left.setParent(node.getParent());
	   }
		// modify subtree
	   node.setLeft(left.getRight());
	   node.getLeft().setParent(node);
	   left.setRight(node);
	   node.setParent(left);
	   node.setSize();
	   left.setSize();

	   return 1;
	}

	/**
	 * Rotating the edge between node and node's right child
	 * @param node upper vertical of the rotated edge
	 * @return 1: # of rebalance operations
	 * Time complexity O(1)
	 */

	public int leftRotation(IAVLNode node){
		IAVLNode right = node.getRight();
		// modify parent
		if (node.getParent() == null) {
			this.root = right;
			right.setParent(null);
		}
		else if (node.getKey() < node.getParent().getKey()) { // node is left child
			node.getParent().setLeft(right);
			right.setParent(node.getParent());
		}
		else { // node is a right child
			node.getParent().setRight(right);
			right.setParent(node.getParent());
		}
		// modify subtree
		node.setRight(right.getLeft());
		node.getRight().setParent(node);
		right.setLeft(node);
		node.setParent(right);
		node.setSize();
		right.setSize();

		return 1;
	}


  /**
   * public int delete(int k)
   *
   * Deletes an item with key k from the binary tree, if it is there.
   * The tree must remain valid, i.e. keep its invariants.
   * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
   * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
   * Returns -1 if an item with key k was not found in the tree.
   * Time complexity O(log(n))
   */

   public int delete(int k){
	   IAVLNode node = getPosition(this.root, k);
	   if (node == null || node.getKey() != k){ // the given key is not in the tree
		   return -1;
	   }
	   else { // given key is found
		   if (this.min_node.getKey() == k) { // node is min
			   this.min_node = getSuccessor(this.min_node);
		   }
		   if (this.max_node.getKey() == k) { // node is max
			   this.max_node = getPredecessor(this.max_node);
		   }

		   if (!node.getLeft().isRealNode() && !node.getRight().isRealNode()){ // node is a leaf
			   if (node.getParent() == null) { // the tree has only root
				   this.root = null;
			   }
			   else if (node.getKey() < node.getParent().getKey()){ // node is a left child
				  node.getParent().setLeft(this.virtual_node);
			   }
			   else {
				   node.getParent().setRight(this.virtual_node);
			   }
		   }
		   else { // node is not a leaf
			   if (!node.getLeft().isRealNode()){ // node has only right child
				   replaceNodes(node, node.getRight());
			   }
			   else if (!node.getRight().isRealNode()){ // node has only left child
					replaceNodes(node, node.getLeft());
			   }
			   else { // node has 2 children
				   IAVLNode successor = getSuccessor(node);
				   IAVLNode successor_parent = successor.getParent();
				   replaceNodes(successor, successor.getRight()); // successor has only right child or is a leaf
				   successor.setLeft(node.getLeft()); // link left child
				   node.getLeft().setParent(successor); // update parent of left child
				   successor.setRight(node.getRight()); // link right child
				   node.getRight().setParent(successor); // update parent of right child
				   if (node.getParent() != null){
					   if (node.getKey() < node.getParent().getKey()){
						   node.getParent().setLeft(successor);
					   }
					   else {
						   node.getParent().setRight(successor);
					   }
					   successor.setParent(node.getParent());
				   }
				   else { // node is the root
					   this.root = successor;
					   successor.setParent(null);
				   }
				   successor.setHeight(node.getHeight());
				   if (successor_parent.getKey() == node.getKey()){
					   return deleteRebalance(successor);
				   }
				   return deleteRebalance(successor_parent);
			   }
		   }
	   }
	   return deleteRebalance(node.getParent());
   }

	/**
	 * Rebalance the tree after a node was deleted
	 * @param node deleted node's parent
	 * @return # of rebalance operation
	 * Time complexity O(log(n))
	 */

	public int deleteRebalance(IAVLNode node){
	   int cnt = 0;
	   while (node != null) {
		   int bf = calculateBalanceFactor(node);
		   node.setSize();
		   int new_height = Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1;
		   boolean height_changed = (node.getHeight() != new_height);
		   if (Math.abs(bf) < 2) {
			   if (!height_changed) {
				   while (node != null){
					   node.setSize();
					   node = node.getParent();
				   }
				   return cnt;
			   }
			   node.setHeight(node.getHeight() - 1);
			   cnt++;
		   }
		   if (bf == -2){
			   if (calculateBalanceFactor(node.getRight()) == 0){
				   node.setHeight(node.getHeight() - 1);
				   node.getRight().setHeight(node.getRight().getHeight() + 1);
				   cnt += leftRotation(node);
				   cnt += 2;
			   }
			   else if (calculateBalanceFactor(node.getRight()) == -1){
				   node.setHeight(node.getHeight() - 2);
				   cnt += leftRotation(node);
				   cnt += 1;
			   }
			   else if (calculateBalanceFactor(node.getRight()) == 1){
				    node.setHeight(node.getHeight() - 2);
					node.getRight().setHeight(node.getRight().getHeight() - 1);
					node.getRight().getLeft().setHeight(node.getRight().getLeft().getHeight() + 1);
					cnt += 3;
					cnt += rightRotation(node.getRight());
				    cnt += leftRotation(node);
			   }
		   }
		   else if (bf == 2){
			   if (calculateBalanceFactor(node.getLeft()) == 0){
				   node.setHeight(node.getHeight() - 1);
				   node.getLeft().setHeight(node.getLeft().getHeight() + 1);
				   cnt += rightRotation(node);
				   cnt += 2;
			   }
			   else if (calculateBalanceFactor(node.getLeft()) == 1){
				   node.setHeight(node.getHeight() - 2);
				   cnt += rightRotation(node);
				   cnt += 1;
			   }
			   else if (calculateBalanceFactor(node.getLeft()) == -1){
				   node.setHeight(node.getHeight() - 2);
				   node.getLeft().setHeight(node.getLeft().getHeight() - 1);
				   node.getLeft().getRight().setHeight(node.getLeft().getRight().getHeight() + 1);
				   cnt += leftRotation(node.getLeft());
				   cnt += rightRotation(node);
				   cnt += 3;
			   }
		   }
		   node = node.getParent();
	   }
	    return cnt;
   }

	/**
	 * @param node the root of the subtree that we want to find the minimum node of
	 * @return the minimum node
	 * Time complexity O(log(n))
	 */

	public IAVLNode getMin(IAVLNode node){
		while (node.getLeft().isRealNode()){
			node = node.getLeft();
		}
		return node;
	}

	/**
	 * @param node
	 * @return successor of node
	 * Time complexity O(log(n))
	 */

	public IAVLNode getSuccessor(IAVLNode node){
		if (node.getRight().isRealNode()){
			return getMin(node.getRight());
		}
		IAVLNode curr_parent = node.getParent();
		while (curr_parent != null && node.getKey() == curr_parent.getRight().getKey()){
			node = curr_parent;
			curr_parent = node.getParent();
		}
		return curr_parent;
	}

	/**
	 * @param node the root of the subtree that we want to find the maximum node of
	 * @return the maximum node
	 * Time complexity O(log(n))
	 */

	public IAVLNode getMax(IAVLNode node){
		while (node.getRight().isRealNode()){
			node = node.getRight();
		}
		return node;
	}

	/**
	 * @param node
	 * @return successor of node
	 * Time complexity O(log(n))
	 */

	public IAVLNode getPredecessor(IAVLNode node){
		if (node.getLeft().isRealNode()){
			return getMax(node.getLeft());
		}
		IAVLNode curr_parent = node.getParent();
		while (curr_parent != null && node.getKey() == curr_parent.getLeft().getKey()){
			node = curr_parent;
			curr_parent = node.getParent();
		}
		return curr_parent;
	}
	/**
	* Replace prev node with updated node
	* Updating the prev parent pointer and the updated parent pointer
	* Does not update children's pointers
	*
	* @param prev current node that will be deleted
	* @param updated the node to place instead of prev
	*
	* Time complexity O(1)
	*/

	public void replaceNodes(IAVLNode prev, IAVLNode updated){
		if (prev.getParent() != null){
			if (prev.getKey() < prev.getParent().getKey()){ // prev is a left child
				prev.getParent().setLeft(updated);
			}
			else { // prev is a right child
				prev.getParent().setRight(updated);
			}
		}
		else {
			this.root = updated;
		}
		updated.setParent(prev.getParent());
	}




	/**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty.
	*
	* Time complexity O(1)
    */
   public String min(){
	   if (this.empty()){
		   return null;
	   }
	   return this.min_node.getValue();
   }

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty.
	*
	* Time complexity O(1)
    */
   public String max() {
	   if (this.empty()){
		   return null;
	   }
	   return this.max_node.getValue();
   }

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   *
   * Time complexity O(n)
   */
  public int[] keysToArray()
  {
	  int[] inOrderKeys = new int[this.size()];
	  IAVLNode currentNode = this.min_node;
	  for (int i = 0; i < this.size(); i++){
		  inOrderKeys[i] = currentNode.getKey();
		  currentNode = getSuccessor(currentNode);
	  }
	  return inOrderKeys;
  }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   *
   * Time complexity O(n)
   */
  public String[] infoToArray()
  {
	  String[] inOrderValues = new String[this.size()];
	  IAVLNode currentNode = this.min_node;
	  for (int i = 0; i < this.size(); i++){
		  inOrderValues[i] = currentNode.getValue();
		  currentNode = getSuccessor(currentNode);
	  }
	  return inOrderValues;
  }

   /**
    * public int size()
    * Returns the number of nodes in the tree.
	*
	* Time Complexity O(1)
    */
   public int size() {
	   if (this.root == null){
		   return 0;
	   }
	   return this.root.getSize();
   }

   
   /**
    * public int getRoot()
    * Returns the root AVL node, or null if the tree is empty
	*
	* Time Complexity O(1)
    */
   public IAVLNode getRoot() {
	   return this.root;
   }
   
   /**
    * public AVLTree[] split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
    * 
	* precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
	*
	* Time complexity O(log(n))
    */   
   public AVLTree[] split(int x) {
	   IAVLNode node_x = this.getPosition(this.root, x);
	   AVLTree small_tree = new AVLTree();
	   AVLTree big_tree = new AVLTree();
	   if (node_x.getLeft().isRealNode()){
		   small_tree = new AVLTree(node_x.getLeft());
	   }
	   if (node_x.getRight().isRealNode()){
		   big_tree = new AVLTree(node_x.getRight());
	   }
	   IAVLNode node = node_x;
	   while(node.getParent() != null){
		   if (node.getParent().getKey() < node.getKey()){ // node is right child
			   if(node.getParent().getLeft().isRealNode()){ // parent has a left child
				   small_tree.join(new AVLNode(node.getParent().getKey(), node.getParent().getValue(), null,
						   this.virtual_node), new AVLTree(node.getParent().getLeft()));
			   }
			   else { // parent has only one child
					small_tree.insert(node.getParent().getKey(), node.getParent().getValue());
			   }
		   }
		   else { // node is left child
			   if(node.getParent().getRight().isRealNode()){ // parent has a right child
				   big_tree.join(new AVLNode(node.getParent().getKey(), node.getParent().getValue(), null,
						   this.virtual_node), new AVLTree(node.getParent().getRight()));
			   }
			   else { // parent has only one child
				   big_tree.insert(node.getParent().getKey(), node.getParent().getValue());
			   }
		   }
		   node = node.getParent();
	   }
	   return new AVLTree[]{small_tree, big_tree};
   }


   /**
    * public int join(IAVLNode x, AVLTree t)
    *
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	*
	* precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
	*
	* Time complexity O(log(n))
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   if (t == null || t.empty()){
		   int to_return = this.getRoot().getHeight() + 2;
		   this.insert(x.getKey(), x.getValue());
		   return to_return;
	   }
	   else if (this.empty()) {
		   int to_return = t.getRoot().getHeight() + 2;
		   this.root = t.getRoot();
		   this.min_node = t.getMin(t.getRoot());
		   this.max_node = t.getMax(t.getRoot());
		   this.insert(x.getKey(), x.getValue());
		   return to_return;
	   }
	   int to_return = Math.abs(this.getRoot().getHeight() - t.getRoot().getHeight()) + 1;
	   if (this.max_node.getKey() < x.getKey()){ // 'this < x < t'
		   this.max_node = t.max_node;
		   if (to_return - 1 == 0 || to_return - 1 == 1){
			   this.getRoot().setParent(x);
			   x.setLeft(this.getRoot());
			   t.getRoot().setParent(x);
			   x.setRight(t.getRoot());
		   }
		   else if (this.getRoot().getHeight() < t.getRoot().getHeight()){
			   IAVLNode node = t.getRoot();
			   while (node.getLeft().isRealNode() && node.getHeight() > this.getRoot().getHeight()){
				   node = node.getLeft();
			   }
			   IAVLNode x_parent = node.getParent();
			   this.getRoot().setParent(x);
			   x.setLeft(this.getRoot());
			   x_parent.setLeft(x);
			   x.setParent(x_parent);
			   node.setParent(x);
			   x.setRight(node);
		   }
		   else {
			   IAVLNode node = this.getRoot();
			   while (node.getRight().isRealNode() && node.getHeight() > t.getRoot().getHeight()) {
				   node = node.getRight();
			   }
			   IAVLNode x_parent = node.getParent();
			   t.getRoot().setParent(x);
			   x.setRight(t.getRoot());
			   x_parent.setRight(x);
			   x.setParent(x_parent);
			   node.setParent(x);
			   x.setLeft(node);
		   }

	   }
	   else { // 't  < x < this'
		   this.min_node = t.min_node;
		   if (to_return - 1 == 0 || to_return - 1 == 1){ // trees are the same height or 1 difference
			   t.getRoot().setParent(x);
			   x.setLeft(t.getRoot());
			   this.getRoot().setParent(x);
			   x.setRight(this.getRoot());
		   }
		   else if (this.getRoot().getHeight() < t.getRoot().getHeight()){
			   IAVLNode node = t.getRoot();
			   while (node.getRight().isRealNode() && node.getHeight() > this.getRoot().getHeight()){
				   node = node.getRight();
			   }
			   IAVLNode x_parent = node.getParent();
			   this.getRoot().setParent(x);
			   x.setRight(this.getRoot());
			   x_parent.setRight(x);
			   x.setParent(x_parent);
			   node.setParent(x);
			   x.setLeft(node);
		   }
		   else {
			   IAVLNode node = this.getRoot();
			   while (node.getLeft().isRealNode() && node.getHeight() > t.getRoot().getHeight()) {
				   node = node.getLeft();
			   }
			   IAVLNode x_parent = node.getParent();
			   t.getRoot().setParent(x);
			   x.setLeft(t.getRoot());
			   x_parent.setLeft(x);
			   x.setParent(x_parent);
			   node.setParent(x);
			   x.setRight(node);
		   }
	   }
	   IAVLNode node = x;
	   while(node.getParent() != null) {
		   node = node.getParent();
	   }
	   this.root = node;
	   x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
	   x.setSize();
	   insertRebalance(x.getParent());
	   return to_return;
   }

	/** 
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{	
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
    	public void setHeight(int height); // Sets the height of the node.
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
		public int getSize(); // Returns the size of the subtree of the node.
		public void setSize(); // Sets the size of the node based on it's children
	}

   /** 
    * public class AVLNode
    *
    * If you wish to implement classes other than AVLTree
    * (for example AVLNode), do it in this file, not in another file. 
    * 
    * This class can and MUST be modified (It must implement IAVLNode).
    */
  public static class AVLNode implements IAVLNode{
	  	private int key;
		private String value;
		private AVLNode left;
		private AVLNode right;
		private AVLNode parent;
		private int height;
		private int size;

	   /**
		* Create a virtual node
		* Time Complexity O(1)
		*/
		public AVLNode(){
			this.key = -1;
			this.value = null;
			this.height = -1;
			this.size = 0;
		}
	   /**
		* Create a new node with given parameters
		* @pre the AVL tree already has a virtual node
		* @param key Key in the tree
		* @param value Node's value (info)
		* @param parent Parent of the created node
		* @param virtual Virtual node of the AVL tree
		* Time Complexity O(1)
		*/
		public AVLNode(int key, String value, AVLNode parent, AVLNode virtual){
			this.key = key;
			this.value = value;
			this.parent = parent;
			this.left =	virtual;
			this.right = virtual;
			this.size = this.left.size + this.right.size + 1;
			this.height = Math.max(this.left.height, this.right.height) + 1;
		}

		@Override
		public int getKey() {
			return this.key;
		}
		@Override
		public String getValue() {
			return this.value;
		}
		@Override
		public void setLeft(IAVLNode node){
			this.left = (AVLNode) node;
		}
		@Override
		public IAVLNode getLeft() {
			return this.left;
		}
		@Override
		public void setRight(IAVLNode node) {
			this.right = (AVLNode) node;
		}
		@Override
		public IAVLNode getRight() {
			return this.right;
		}
		@Override
	    public void setParent(IAVLNode node) {
			this.parent = (AVLNode) node;
		}
		@Override
		public IAVLNode getParent() {
			return this.parent;
		}
		@Override
		public boolean isRealNode() {
			return this.key != -1;
		}
	    @Override
		public void setHeight(int height) {
			this.height = height;
	    }
		@Override
	    public int getHeight() {
			return this.height;
	    }
	   @Override
	   public int getSize() {
		   return this.size;
	   }
	   @Override
	   public void setSize(){
			this.size = this.left.size + this.right.size + 1;
	   }
  }

}
  
