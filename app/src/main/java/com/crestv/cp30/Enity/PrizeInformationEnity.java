package com.crestv.cp30.Enity;

import java.util.List;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class PrizeInformationEnity {

    /**
     * msg : 查询成功
     * status : 1
     * data : {"list":[{"id":100,"nickName":"再醉、孤酒","avator":"http://file.86580.cn:9001/syptapp/http://q.qlogo.cn/qqapp/1105541425/B21A2A8DC3344E061CB48003275B933D/100","ranking":1,"income":0},{"id":118,"nickName":"哦哦(@_@)","avator":"http://file.86580.cn:9001/syptapp/avator/noavator.png","ranking":2,"income":0},{"id":101,"nickName":"子逸","avator":"http://file.86580.cn:9001/syptapp/http://q.qlogo.cn/qqapp/1105541425/61A5E85B10AE40EFA321E32DDE66D0BD/100","ranking":3,"income":0}],"listHero":[{"id":100,"nickName":"再醉、孤酒","avator":"http://file.86580.cn:9001/syptapp/http://q.qlogo.cn/qqapp/1105541425/B21A2A8DC3344E061CB48003275B933D/100","ranking":1,"income":0},{"id":118,"nickName":"哦哦(@_@)","avator":"http://file.86580.cn:9001/syptapp/avator/noavator.png","ranking":2,"income":0},{"id":101,"nickName":"子逸","avator":"http://file.86580.cn:9001/syptapp/http://q.qlogo.cn/qqapp/1105541425/61A5E85B10AE40EFA321E32DDE66D0BD/100","ranking":3,"income":0}]}
     */

    private String msg;
    private int status;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<ListBean> list;
        private List<ListHeroBean> listHero;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public List<ListHeroBean> getListHero() {
            return listHero;
        }

        public void setListHero(List<ListHeroBean> listHero) {
            this.listHero = listHero;
        }

        public static class ListBean {
            /**
             * id : 100
             * nickName : 再醉、孤酒
             * avator : http://file.86580.cn:9001/syptapp/http://q.qlogo.cn/qqapp/1105541425/B21A2A8DC3344E061CB48003275B933D/100
             * ranking : 1
             * income : 0
             */

            private int id;
            private String nickName;
            private String avator;
            private int ranking;
            private int income;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getAvator() {
                return avator;
            }

            public void setAvator(String avator) {
                this.avator = avator;
            }

            public int getRanking() {
                return ranking;
            }

            public void setRanking(int ranking) {
                this.ranking = ranking;
            }

            public int getIncome() {
                return income;
            }

            public void setIncome(int income) {
                this.income = income;
            }
        }

        public static class ListHeroBean {
            /**
             * id : 100
             * nickName : 再醉、孤酒
             * avator : http://file.86580.cn:9001/syptapp/http://q.qlogo.cn/qqapp/1105541425/B21A2A8DC3344E061CB48003275B933D/100
             * ranking : 1
             * income : 0
             */

            private int id;
            private String nickName;
            private String avator;
            private int ranking;
            private int income;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public String getAvator() {
                return avator;
            }

            public void setAvator(String avator) {
                this.avator = avator;
            }

            public int getRanking() {
                return ranking;
            }

            public void setRanking(int ranking) {
                this.ranking = ranking;
            }

            public int getIncome() {
                return income;
            }

            public void setIncome(int income) {
                this.income = income;
            }
        }
    }
}
