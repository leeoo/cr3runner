# Подготовка #

Начнем с того, что запуск программы предполагается на Kindle 4 (без тачскрина) **с версией прошивки 4.0.1**.

## root-доступ ##

Первым делом требуется получить root-доступ к устройству. Об этом много написано в других источниках. Поэтому только конспективно.

На подсоединенном к компьютеру по USB Kindle переименовываем файл (или просто создаем файл с именем ENABLE\_DIAGS) для перезагрузки
устройства в диагностический режим:

```
rename DONT_HALT_ON_REPAIR ENABLE_DIAGS
```

Включаем USBNetworking: "Misc individual diagnostics -> Utilities -> Enable USBnet".

Лично я работал из под Windows 7. Драйвер для USBNetworking в Windows 7 уже есть. Вариантов для его активации много.
Мне потребовалось только немного обмануть Windows.

Для этого я "подсунул" немного подправленный файл linux.inf (выкладываю для скачивания).

Этот файл специфичен именно для Windows 7 (64 bit). Тут ключевое слово - это NTamd64.

Настраиваем IP-адрес для полученного соединения следующим образом (подробности опускаем):

![http://cr3runner.googlecode.com/svn/wiki/images/ipwin.png](http://cr3runner.googlecode.com/svn/wiki/images/ipwin.png)

Следующий этап - это подбор пароля. Для прошики 4.0.0 - это просто "mario". Для версии прошивки 4.0.1 нам потребуется серийный номер Kindle (ищется в настройках)
и лучше всего доступ к Linux с Python. Это просто оказалось самым простым лично для меня.
Другие варианты: установить Python для Windows, переписать для другого языка и т.п.

Под Linux все просто:

```
victor@host:~$ python
Python 2.5.2 (r252:60911, Oct  5 2008, 19:29:17)
[GCC 4.3.2] on linux2
Type "help", "copyright", "credits" or "license" for more information.
>>> import hashlib
>>> print("fiona%s"%hashlib.md5("B00E150114043GLR\n".encode('utf-8')).hexdigest()[7:11])
fiona8721
>>>
```

Итак пароль для root-а fiona8721.

Далее просто соединяемся любым SSH-клиентом используя адрес 192.168.15.244, имя root и полученный выше пароль. Я использовал PuTTY:

![http://cr3runner.googlecode.com/svn/wiki/images/putty.png](http://cr3runner.googlecode.com/svn/wiki/images/putty.png)

Для загрузки файлов на устройство из под Windows, а так же для редактирования удобно использовать Far c плагином WinSCP:

![http://cr3runner.googlecode.com/svn/wiki/images/scp.png](http://cr3runner.googlecode.com/svn/wiki/images/scp.png)

Обратите внимание, что выбран протокол SCP (по-умолчанию стоит SFTP) иначе работать не будет.

![http://cr3runner.googlecode.com/svn/wiki/images/far.png](http://cr3runner.googlecode.com/svn/wiki/images/far.png)

Все, root-доступ получен.

## Выдаем права доступа ##

Все, что делается дальше надо делать осторожно. Лучше если с пониманием происходящего.

Выполняем монтирование для получения доступа к файловой системе Kindle:

```
cd /mnt/us
mkdir /mnt/main
mount -t ext3 -o rw /dev/mmcblk0p1 /mnt/main
```

Разрешаем киндлету доступ к требуемым нам каталогам. Для этого редактируем файл /mnt/main/opt/amazon/ebook/security/external.policy

Сделать это можно с помощью того же Far. В конец файла добавляем строки:

```
grant {
  permission java.io.FilePermission "/mnt/us/qtKindle/-", "read,execute";
  permission java.io.FilePermission "/mnt/us/cr3runner/-", "read,write,execute";
  permission java.io.FilePermission "/mnt/us/cr3/-", "read,execute";
};
```

Скачиваем файл developer.keystore и помещаем его в папку /var/local/java/keystore.

Размонтируем после сделанных изменений:

```
sync
umount /mnt/main
rm -r  /mnt/main
```

Выходим из диагностического режима

## Установка Cool Reader 3 ##

Далее работаем с подсоединенным к компьютеру по USB Kindle.

Распаковываем CR3 в корень. При этом там появляются папки cr3 и qtKindle.

Папку qtKindle меняем на новую версию Qt, например, на момент написания [отсюда](http://www.mediafire.com/?qzdy7xdi550pd6s) или [отсюда](http://narod.ru/disk/38773397001/qtKindle_k3_k4.zip.html) (copyright by andy wooden).

## Установка CR3Runner ##

Скачиваем файл cr3runner-2.5.azw2 и кидаем его в папку documents на Kindle.

Создаем в корне паку cr3runner и помещаем туда скаченный файл commands.txt

Этот файл может **потенциально** использоваться для запуска и других программ.

Ну и пытаемся все это дело запустить...

Мне удалось. Движение вперед по пути улучшения тоже идет, но медленно.

Это только некий концепт.