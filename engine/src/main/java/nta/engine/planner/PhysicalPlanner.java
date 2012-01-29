/**
 * 
 */
package nta.engine.planner;

import java.io.IOException;

import nta.engine.SubqueryContext;
import nta.engine.exception.InternalException;
import nta.engine.ipc.protocolrecords.Fragment;
import nta.engine.planner.logical.GroupbyNode;
import nta.engine.planner.logical.LogicalNode;
import nta.engine.planner.logical.LogicalRootNode;
import nta.engine.planner.logical.ProjectionNode;
import nta.engine.planner.logical.ScanNode;
import nta.engine.planner.logical.SelectionNode;
import nta.engine.planner.physical.GroupByExec;
import nta.engine.planner.physical.PhysicalExec;
import nta.engine.planner.physical.SeqScanExec;
import nta.storage.StorageManager;

/**
 * This class generates a physical execution plan.
 * 
 * @author Hyunsik Choi
 * 
 */
public class PhysicalPlanner {
  private final StorageManager sm;

  public PhysicalPlanner(StorageManager sm) {
    this.sm = sm;
  }

  public PhysicalExec createPlan(SubqueryContext ctx, LogicalNode logicalPlan)
      throws InternalException {
    PhysicalExec plan = null;
    try {
      plan = createPlanRecursive(ctx, logicalPlan);
    } catch (IOException ioe) {
      throw new InternalException(ioe.getMessage());
    }

    return plan;
  }

  private PhysicalExec createPlanRecursive(SubqueryContext ctx,
      LogicalNode logicalNode) throws IOException {
    PhysicalExec outer = null;
    PhysicalExec inner = null;

    switch (logicalNode.getType()) {
    case ROOT:
      LogicalRootNode rootNode = (LogicalRootNode) logicalNode;
      return createPlanRecursive(ctx, rootNode.getSubNode());

    case SELECTION:
      SelectionNode selNode = (SelectionNode) logicalNode;
      return createPlanRecursive(ctx, selNode.getSubNode());

    case PROJECTION:
      ProjectionNode prjNode = (ProjectionNode) logicalNode;
      return createPlanRecursive(ctx, prjNode.getSubNode());

    case SCAN:
      inner = createScanPlan(ctx, (ScanNode) logicalNode);
      return inner;

    case GROUP_BY:
      GroupbyNode grpNode = (GroupbyNode) logicalNode;
      PhysicalExec subOp = createPlanRecursive(ctx, grpNode.getSubNode());   
      return createGroupByPlan(ctx, grpNode, subOp);
      
    case JOIN:   
    case RENAME:
    case SORT:
    case SET_UNION:
    case SET_DIFF:
    case SET_INTERSECT:
    case CREATE_TABLE:
    case INSERT_INTO:
    case SHOW_TABLE:
    case DESC_TABLE:
    case SHOW_FUNCTION:
    default:
      return null;
    }
  }

  public PhysicalExec createScanPlan(SubqueryContext ctx, ScanNode scanNode)
      throws IOException {
    Fragment fragment = ctx.getTable(scanNode.getTableId());
    SeqScanExec scan = new SeqScanExec(sm, scanNode, fragment);

    return scan;
  }
  
  public PhysicalExec createGroupByPlan(SubqueryContext ctx, 
      GroupbyNode groupbyNode, PhysicalExec subOp) throws IOException {
    GroupByExec groupby = new GroupByExec(sm, groupbyNode, subOp);
    
    return groupby;
  }
}