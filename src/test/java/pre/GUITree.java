package pre;

import javax.swing.JFrame;

import javax.swing.JTree;

import javax.swing.event.TreeSelectionEvent;

import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultMutableTreeNode;

import javax.swing.tree.TreePath;

public class GUITree extends JFrame {

    DefaultMutableTreeNode root = null;

    JTree tree = null;

    public GUITree() {

        super();

        root = new DefaultMutableTreeNode("root");

        tree = new JTree(root);

        this.add(tree);

       // 被选中监听事件

        tree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) e.getNewLeadSelectionPath()

                        .getLastPathComponent();

                System.out.println("Current selected: " + selectedNode + ", leaf: " + selectedNode.isLeaf()

                        + ", originObj: " + selectedNode.getUserObject().getClass());

            }

        });

        this.setSize(200, 600);

        this.setVisible(true);

    }

 public DefaultMutableTreeNode addPathNode(String pathnode) {

        String[] ns = pathnode.split("\\.");

        DefaultMutableTreeNode node = root;

        for (String n : ns) {

            int i = node.getChildCount() - 1;

            for (; i >= 0; i--) {

                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) node.getChildAt(i);

                if (tmp.getUserObject().equals(n)) {

                    node = tmp;

                    break;

                }

            }

            if (i < 0) {

                DefaultMutableTreeNode tmp = new DefaultMutableTreeNode(n);

                node.add(tmp);

                node = tmp;

            }

        }

        return node;

    } 

    public DefaultMutableTreeNode addPathNode(String path, Object node) {

        DefaultMutableTreeNode parentNode = addPathNode(path);

        DefaultMutableTreeNode ret = new DefaultMutableTreeNode(node);

        parentNode.add(ret);

        return ret;

    }

    public static void main(String[] args) {

        GUITree me = new GUITree();

        me.addPathNode("中国.北京.海淀");

        me.addPathNode("中国.河北");
        me.tree.expandPath(new TreePath(me.root));
        me.addPathNode("美国.纽约.华盛顿.伦敦.澳门.香港");  
//        me.tree.expandPath(new TreePath(me.root));
        me.tree.updateUI();
//        me.repaint();


    } 

}
