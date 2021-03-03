package com.river.learn.java.design.state;

/**
 * 状态模式
 * @author 17822
 */
public class ActiveContext {

    private State state;

    private Apply apply = new Apply(this);
    private Approval approval = new Approval(this);
    private End end = new End();

    public void doApply(){
        state.apply();
    }

    public void doApproval(){
        if(state instanceof Approval){
            state.approval();

            if(state instanceof  End){
                state.end();
            }

        }else {
            System.err.println("当前案件不属于审批节点");
        }
    }

    ActiveContext (int i){
        this.state = this.getApply();
        System.err.println(i);
    }

    ActiveContext (){
        this.state = this.getApply();
    }

    public Apply getApply() {
        return apply;
    }

    public void setApply(Apply apply) {
        this.apply = apply;
    }

    public Approval getApproval() {
        return approval;
    }

    public void setApproval(Approval approval) {
        this.approval = approval;
    }

    public End getEnd() {
        return end;
    }

    public void setEnd(End end) {
        this.end = end;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
