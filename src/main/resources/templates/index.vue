<template>
  <div class="bg">
    <div class="registerPage">
      <div class="r-title">云课堂</div>

      <div class="r-text">用户登录</div>
      <div id="register">
        <div class="register-form">
          <ul>
            <li>
              <div>学工号：</div>
              <input type="text" placeholder="学工号" name="userId" v-model="loginData.userId" />
            </li>
            <li>
              <div>密码：</div>
              <input type="password" placeholder="密码" name="password" v-model="loginData.password" />
            </li>
          </ul>
          <button type="submit" @click="submitForm()">登录</button>
        </div>
      </div>
      <a class="forget" href="javascript:;" @click="showChangePage()">忘记密码,点击修改</a>
    </div>
    <div class="cover" v-show="this.isChange"></div>
    <div class="forgetPage" v-show="this.isChange">
      <ul>
        <li>
          <div>学工号：</div>
          <input type="text" name="userId" placeholder="请输入学工号" v-model="changeData.userId" />
        </li>
        <li>
          <div>新密码：</div>
          <input type="password" name="password" placeholder="请输入新密码" v-model="changeData.password" />
        </li>
        <li>
          <div>验证码：</div>
          <input type="text" name="code" placeholder="验证码" v-model="changeData.code" />
          <!-- 后端传递的验证码图片 -->
          <img :src="imgcode"/>
        </li>
      </ul>
      <button @click="confirmChange()">确认修改</button>
      <button @click="cancelChange()">取消修改</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import qs from "qs";
export default {
  data() {
    return {
      imgcode:'',
      uid:'',
      //登录信息
      loginData: {
        userId: "",
        password: ""
      },
      //修改密码信息的页面
      changeData: {
        userId: "",
        password: "",
        code: ""
      },
      //是否显示修改密码的页面
      isChange: false
    };
  },
  
  methods: {
    //登录
    submitForm() {
      var loginuser = {
        userId: this.loginData.userId,
        password: this.loginData.password
      };
      const that=this;
      axios
        .post(
          "http://120.26.185.223:8888/user/login.do",
          qs.stringify(loginuser)
        )
        .then(function(res) {
          console.log(qs.stringify(loginuser))
          if (res.data.success) {
            if (res.data.data.role == "2") {
              that.$router.push({ path: "/teacher" });
            }
            if (res.data.data.role == "1") {
              that.$router.push({ path: "/student" });
            }
          } else {
            console.log(res.data.msg);
          }
        })
        .catch(function(err) {
          console.log(err);
        });
    },
    //点击修改密码显示页面，同时发送请求，获取后端验证码
    showChangePage() {
       var uid=prompt("请输入学工号","");
       const that=this;
       axios.get(
          "http://120.26.185.223:8888/user/creatCheckCode.do",
          // {responseType: 'blob'},
          {params: {userId: uid}},

        ).then(res => {
          console.log(res)
          let blob = res.data;
          // let src = window.URL.createObjectURL(blob)//转换为图片路径
          // that.imgcode=src;
          // console.log(src)
          //自定义名字 imgUrl 将请求回来的图片信息放到里面
          
        })

          // console.log(res.data);
          // console.log(typeof(res.data))
         
          
    
        .catch(function(err) {
          console.log(err);
        });
      this.isChange = true;
    },
    //确认修改按钮
    confirmChange() {
      //发送请求
    },
    //取消修改按钮
    cancelChange() {
      this.isChange = false;
    }
  }
  // http://120.26.185.223:8888/user/login.do
  // http://mengxuegu.com:7300/mock/5e89e8d21c5f7f209577b8c9/example/user/login.do
};
</script>


<style scoped>
html,
body {
  border: none;
}
html {
  font-size: 100px;
  width: 100%;
  height: 100%;
}
body {
  font-size: 16px;
  width: 100%;
  height: 100%;
}
.bg {
  width: 100%;
  height: 100%;
  background-color: lightskyblue;
}
.r-title {
  height: 1rem;
  font-size: 1rem;
  padding-left: 3.4rem;
  padding-top: 0.3rem;
  padding-bottom: 0.3rem;
}
.r-text {
  height: 0.8rem;
  font-size: 0.7rem;
  color: white;
  margin-left: 0.5rem;
  margin-top: 1rem;
}
#register {
  margin-top: 0.5rem;
}
.register-form li {
  display: block;
  width: 100%;
  height: 1.3rem;
  background-color: #fff;
  list-style: none;
  font-size: 0.4rem;
  line-height: 1.3rem;
  border-bottom: 1px solid silver;
}
.register-form li div,
.register-form li input {
  display: inline-block;
}
.register-form li div {
  width: 2rem;
  height: 1rem;
  padding-left: 0.3rem;
}
.register-form li input {
  border-style: none;
  outline: none;
  width: 3.5rem;
  height: 1rem;
  font-size: 0.35rem;
  padding-left: 0.1rem;
}
.register-form button {
  width: 2.2rem;
  height: 1.2rem;
  background-color: deepskyblue;
  color: white;
  border-radius: 0.2rem;
  margin-top: 1rem;
  margin-left: 4rem;
  font-size: 0.4rem;
  outline: none;
  border-style: none;
  border: 1px solid silver;
}
.register-form a {
  display: block;
  font-size: 0.3rem;
  margin: 0 1.65rem;
}
/* 弹出的忘记密码页面 */
.registerPage {
  width: 100%;
  height: 100%;
  position: absolute;
  z-index: 0;
}
.cover {
  width: 100%;
  height: 100%;
  position: absolute;
  background-color: #000;
  opacity: 0.3;
  z-index: 1;
}
.forget {
  text-decoration: none;
  color: blue;
  display: inline-block;
  margin-left: 3.2rem;
  margin-top: 0.5rem;
  font-size: 0.45rem;
}
.forgetPage {
  position: relative;
  z-index: 2;
  width: 8rem;
  height: 8rem;
  top: 5rem;
  left: 1rem;
  background-color: rgb(85, 73, 73);
  border: 1px solid silver;
  font-size:.45rem;
  border-radius:4px;
}
.forgetPage img {
  width: 2rem;
  height: 0.5rem;
}
.forgetPage ul {
  margin-top: 0.3rem;
  margin-left: 1rem;
}
.forgetPage li {
  list-style: none;
  height: 2rem;
}
.forgetPage input{
  width:4.5rem;
  height:.8rem;
  margin-top:.2rem;
  border-style:none;
  border:1px solid silver;
  border-radius:4px;
  padding-left:.2rem;
}
.forgetPage button {
  margin-top:.5rem;
  margin-left: 0.9rem;
  border:none;
  width:2.5rem;
  height:.8rem;
  font-size:.4rem;
  border-radius:3px;
}

.inputId{
  position:absolute;
  z-index:2;
}
</style>



