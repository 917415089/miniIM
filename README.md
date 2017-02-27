


# **<span id="MINI">MiniIM项目</span>**

[netty的项目小结](http://blog.csdn.net/u011518120/article/details/57919395)下面的内容是我这篇blog的节选。


## **项目概述**
这个程序现在来看其实挺失败的，因为没有充分利用netty的特性，只是用到了皮毛，和直接用socket去写也没什么区别。但是无论好坏算是我的第一个个人项目，总结还是必要的。而且作为一个单纯的IM项目来说基本还是完成了的。

首先项目用的应用层协议websocket，不过后来觉得根本没毕业。当初做的时候主要是希望能否做成web和客户端双端的那种，不过后来发现还是当初太傻。
数据序列化基本上都是JSON，这里用的fastJSON的包做解析，JSON的内容都在JSON的包中定义。
自定的hander里面采用的是状态模式，主要是验证和加密，如果都通过则会进入最终的状态也就是互相发json来进行通信。完成任务交互。
基本的内容差不多就是这样。下面还是用图来说明比较容易看懂。

线程模型：
![MiniIM线程模型](http://img.blog.csdn.net/20170227210222470?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTUxODEyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

netty的线程模型都差不多。只是根据任务的类型来确定线程池就好。

### **服务器端**

**服务器端的状态转移图**：

![服务器的状态模式](http://img.blog.csdn.net/20170227210804457?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTUxODEyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

这部分主要是工作线程组的任务，建立连接后会创建一个ServerStatementMangement的对象，然后就是状态模式，定义了几个状态ServerInitState、SelectAlgorithmandPubKey、ServerACK和ServerDealwithJSON几个状态。目前这几个状态主要完成的任务类似于ssl，代码比较好懂看看就行。在ServerDealWithJSON状态下就说明通过了验证，这时候会创建一个DealWithJSON实例，然后该连接（回话）内的所有JSON的处理都交给这个DealWithJSON实例来完成。
当初为何用状态模式，其实还是自己想的比较多，我是想把这个软件做成一个比较多功能的东西，目前就好像是可以聊天的软件，但是比如视频的时候或者像刷微博的时候接受的json肯定是不一样的。需要对发送和接受的json和状态进行限定，这样使用状态模式如果将来需要拓展只需要加不同的状态就行了。然后针对状态编程就好。如果需要废弃功能也只需要把状态转移图的入口删掉就好。简单的说就是加强可拓展性。可惜人算不如天算哈哈。。

主要注意的是所有的用户信息都是保存在ServerStateManagement中的，所以后续DealWithJSON需要从这里获取数据。另外所有状态的输入和输出都是String，这里写是JSON其实并不完全，在加密之前其实就是普通的JSON的字符串形式，但是加密后是需要解密才能得到有效的信息的。具体看看代码就好。

**服务器工作流程图**：
![服务器工作流程图](http://img.blog.csdn.net/20170227213436185?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTUxODEyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
这个工作流程比较麻烦了，首先我们知道DealWithJSON是在连接完成验证的阶段创建的，每个连接只有一个。这个DealWithJSON会受到来自于客户端传来的JSON信息然后进行处理，也就是业务逻辑的主体部分，当然这里做了细分，如果该任务不需要数据库任务就会把直接进行处理然后转发，比如说聊天的互相发信息时候就是这这样的。
如果是需要访问数据的任务（比如好友列表等等）则DealWithJSON则会把任务包装成一个DBCallable类的实例存入数据库缓存队列中，会有另一个全局的数据库线程池来不停的从这个DBCallble阻塞队列中去任务执行，然后再打包成SendBackJSON类型的数据，然后放入SendBackJSON阻塞队列。然后同样的套路，会有一个全局的回写线程SendBackThreadPool从阻塞队列中取SendBackJSON，SendBackJSON中会包含有当初DealWithJSON中的Channel的信息，结合一个Channel散列表可以确定写回那个Channel。
此外还有一个问题就是怎么发离线信息，这里我实际上做了一个离线的数据库，比如说好友申请或者离线信息，如果在写入信息的时候发现需要写入的通道不存在（即用户离线），则需要将这个JSON写回到数据库中，在每次用户登录的时候回查询这个离线数据库是否有需要写回给用户的离线信息。这里其实用一个全局Channel散列表来写回是一个非常二的行为。具体失败的地方后面再总结。

### **客户端**

服务器端看完了客户端基本上是类似的，这里就直画个图不详细说明了。

**客户端的状态模式**：

![客户端的状态模式](http://img.blog.csdn.net/20170227213831987?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTUxODEyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

**客户端的工作流程图**：

![客户端的工作流程图](http://img.blog.csdn.net/20170227213900426?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMTUxODEyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

首先也是和服务器一样状态模式，然后再进入最后阶段的时候会创建两个单线程的线程池，一个用来处理接受到的JSON一个用来处理发送JSON的过程，这里之所以和服务器不同主要是考虑到有可能多开，但是后来发现想多了，不过这样也可以工作就没改。
然后是一个ClientMange类，这个类是一个静态类。包括了GUI和发送和返回的阻塞队列，这两个阻塞队列会和创建的两个单线程线程池配合工作，完成信息的交互，GUI用的就是swing。



## **主要的几个问题**

首先如前面第一小节中说的Netty主要用来做什么，其实从一开始在写的时候就没搞清楚就乱写了。

首先最大的问题是没有很好的利用netty：

比如说我想做一个文件传输的功能，如果是小文件的话当然可以用json包装后传输，但是如果是比较大一些的问题在这样做会影响通信。这时候我想到的是做一个带外传输的功能。但是在我现在的这模式下做带外是比较麻烦的，所以我才说这样做失败了，如果让我重新设计功能，我会用netty做一个类似网关的功能，对于文件传输的功能可以在客户端单独开一个线程，进行带外传输，在服务器端做一个网关，如果是聊天服务器的业务逻辑信息就传给工作线程的服务器，如果是传输的是文件就把信息传给处理文件的服务器，当然这里不一定是服务器也许是同一个服务器的不同端口也一样的道理。但是在现有的模式上做网关要改的东西太多了。这里就没改，而且都用json做包装也是不太合适的传文件的。

此外还有一个很失败的地方就是破坏了IO读写Channel的特性。我这里用一个Channel的散列表（其实是一堆散列表）把用户名-Channel一一对应起来，然后写回的时候就可以根据用户名来找到Channel然后调用Channel的write写入信息了。这样做当然是可以的，但是问题是对Channel的读写就不是IO线程独有的权限了。而且还有可能会发生同步的错误的问题。这其实是非常危险的。而应该吧任务都包装给IO读写线程去做，不过这部分究竟该如何去做我还不是太清楚。netty很多地方还是不是太明白。

## **下一步的工作**

接下来的问题，这个项目该不该继续做下去的问题，

首先，如果从IM的角度来说实现这个只是实现了基本的功能，还存在IO线程写回的大隐患应该是要改一改的，但是如果继续下去只能越走越歪。和当初学习netty的初衷相悖了。

其次，如果希望进一步熟悉netty的角度来看，项目应该继续，不过不在是现在的形式而是需要改很多，如我在第一小节中说过的，如果现在让我重新写一个netty的练手项目我不会在写这么多无关的业务逻辑程序，可以只写一个网关或者只写一个socksv5的实现这样。不然不能算是理解的netty。这个项目本身其实跟netty关系并不大，netty如果从使用的角度来说主要还是会写处理器和编码解码器。但是这样基本上就算是另一个完全不同的项目了。

最后，如果从增强编程能力的角度来说，我觉得应该把重点放在java的NIO上面，这样应该会对编程有更深入的了解，学习netty毕竟只是增强编程能力的一个载体。把NIO的源码看一看从深入的角度说更好一些。

现在到底该怎么办我还没完全想好，不过个人比较倾向于把主要精力放在NIO上，因为之前看容器和并发的源码感觉学到的东西比较有深度，最近杂事也比较多。
