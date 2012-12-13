Androrm - An ORM for android
============================

## What is androrm?

Androrm is a ORM (Object Relational Mapper) for android. This means, that it will take care of all the database stuff for you like creating the right tables. It will also write your queries for you! Of course there are already really good solutions for plain Java, that you would run on a server back-end, but these would be just too much for your small phone.

Anyway, even on a phone the number of tables and the complexity of queries can easily become overwhelmingly large. That is where androrm can help you, but beware, this help does not come without a cost. Androrm uses reflection, to analyze your classes and to create the correct tables and queries. This takes some time. So if you need every millisecond of computing time, then androrm will probably not fit your needs. But if you, for example, have an app, that loads data from some server and displays it, these few extra milliseconds probably won't count.

## Installation

    wget http://www.androrm.com/downloads/latest/tarball/
    tar -xvzf androrm_0.4.1.tar.gz
    cd androrm_0.4.1
    mv androrm.jar /path/to/your/lib/folder

## Registering your models

After you added the library to your classpath you still have to tell androrm which classes, it should manage. Unfortunately you can't just hand in a package name at this stage. But this support should come in future releases. For now, there are two steps you have to take.

### 1. Choose a name for your database

By default androrm will create a database called my_database. If you want to change that name for any reason (this might be handy if you create test databases), you can easily do so.

    DatabaseAdapter.setDatabaseName("My_Database_Name");

### 2. Register your models

The second and also the last step is to tell androrm exactly which classes you want to have managed. You only have to do this once, preferably in your launcher activity.

    List<Class<? extends Model>> models = new ArrayList<Class<? extends Model>>();
    models.add(FirstModel.class);
    models.add(SecondModel.class);

    DatabaseAdapter adapter = DatabaseAdapter.getInstance(getApplicationContext());
    adapter.setModels(models);

After these two steps, you are set up and ready to go!