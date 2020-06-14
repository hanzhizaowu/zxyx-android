# 社交App-朝夕印象

app：项目代码
<br>library：封装的公共部分代码

## 应用简介：
基本功能包括用户模块、帖子模块、视频模块、发图模块、私信模块

## 技术选型
下面列举技术栈，并说明选择的原因:
* 使用MVVM架构整个项目，对应于ui、viewmodel、repository三个目录
* Base基础类的封装
* 使用RxJava配合Retrofit2做网络请求
* PhotoPicker图片选择器
* FFMPEG视频处理，IJKPlayer视频播放
* 使用Glide做图片的处理和加载
* 使用SwipeRefreshLayout/RecyclerView实现下拉刷新、上拉加载，以及瀑布流
* eventBus事件总线的使用

## License
Copyright (c) 2020 czx
<br>个人练习使用，素材均来源网络，如有侵权请告知，立马删除。如果因他人下载使用产生纠纷均与本人无关。