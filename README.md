```
连接超时后，server主动断开
connector.setIdleTimeout(15 * 1_000L);

测试延时一段时间之后，发送报文
判断connector的idleTimeout是否生效


```