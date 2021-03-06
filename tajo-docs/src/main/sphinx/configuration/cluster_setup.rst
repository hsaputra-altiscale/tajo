*******************************************
Cluster Setup
*******************************************

Fully Distributed Mode
==========================================
A fully distributed mode enables a Tajo instance to run on `Hadoop Distributed File System (HDFS) <http://wiki.apache.org/hadoop/HDFS>`_. In this mode, a number of Tajo workers run across a number of the physical nodes where HDFS data nodes run.


In this section, we explain how to setup the cluster mode. 


Settings
--------------------------------------------------------

Please add the following configs to tajo-site.xml file:

.. code-block:: xml

  <property>
    <name>tajo.rootdir</name>
    <value>hdfs://hostname:port/tajo</value>
  </property>

  <property>
    <name>tajo.master.umbilical-rpc.address</name>
    <value>hostname:26001</value>
  </property>

  <property>
    <name>tajo.master.client-rpc.address</name>
    <value>hostname:26002</value>
  </property>

  <property>
    <name>tajo.catalog.client-rpc.address</name>
    <value>hostname:26005</value>
  </property>


Make base directories and set permissions
--------------------------------------------------------

If you want to know Tajo’s configuration in more detail, see Configuration page.
Before launching the tajo, you should create the tajo root dir and set the permission as follows: ::

  $ $HADOOP_HOME/bin/hadoop fs -mkdir       /tajo
  $ $HADOOP_HOME/bin/hadoop fs -chmod g+w   /tajo


Launch a Tajo cluster
--------------------------------------------------------

Then, execute ``start-tajo.sh`` ::

  $ $TAJO_HOME/bin/start-tajo.sh

.. note::

  In default, each worker is set to very little resource capacity. In order to increase parallel degree, please read 
  :doc:`/configuration/worker_configuration`.

.. note::

  In default, TajoMaster listens on 127.0.0.1 for clients. To allow remote clients to access TajoMaster, please set tajo.master.client-rpc.address config to tajo-site.xml. In order to know how to change the listen port, please refer :doc:`/configuration/configuration_defaults`.

