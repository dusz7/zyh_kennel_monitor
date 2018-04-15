# coding=UTF-8

# import Adafruit_DHT as dht
import threading as thd
import time
import queue
import copy
import urllib.request
import http.client
import json

# 该笼子的id
kennel_id = '0002'
# 传感器串口编号
dht_pin = 4

# 笼子状态
state_normal = 0
state_operation = 1
state_warn = 2
state_now = state_normal
state_pre = state_now

# 数据阈值
temperature_warn = 26.7
temperature_operation = 23.9
temperature_normal = 18.3

# 时间间隔（秒）
read_data_time = 2.5
send_data_time = 300

# 假的数据
temperature = temperature_normal
humidity = 30.5
t_list = [0,0,0,0,0,0]
h_list = [0,0,0,0,0,0]

def read_data_from_sensor():
	global temperature
	global humidity

	while 1:
		# humidity,temperature = dht.read(dht.DHT22, dht_pin)
		temperature = temperature + 0.2
		print('Temp={0:0.1f}*C  Humidity={1:0.1f}%RH'.format(temperature, humidity))

		analyse_sensor_data()

		time.sleep(read_data_time)

def send_data_to_bmob():
	global t_list
	global h_list
	global temperature
	global humidity

	while 1:
		conn = http.client.HTTPConnection("api.bmob.cn")
		# Bmob 请求头
		headerdata = {"X-Bmob-Application-Id":"ce623e8d32661f07cc185862722ca16f",
		"X-Bmob-REST-API-Key":"25d230fee5ced3b97d4ea6d11321eaae",
		"Content-Type": "application/json"}
		# 数据表的位置
		requrl = "/1/classes/KennelState"

		t_list.pop(0)
		h_list.pop(0)
		t_list.append(temperature)
		h_list.append(humidity)

		# 上传数据
		upload_data = {'kennelId':kennel_id,
		'temperature1':t_list[0],'humidity1':h_list[0],
		'temperature2':t_list[1],'humidity2':h_list[1],
		'temperature3':t_list[2],'humidity3':h_list[2],
		'temperature4':t_list[3],'humidity4':h_list[3],
		'temperature5':t_list[4],'humidity5':h_list[4],
		'temperature6':t_list[5],'humidity6':h_list[5]
		}
		# 此处将数据转换成JSON格式才能提交，不然会返回107错误
		upload_data = json.dumps(upload_data)
		# 先查询
		conn.request(method="GET",url=requrl+'?where={"kennelId":"'+kennel_id+'"}',headers = headerdata)
		response = conn.getresponse()
		res = response.read()
		search_result = json.loads(res)
		if len(search_result['results']):
			conn.request(method="PUT",url=requrl+'/'+search_result['results'][0]['objectId'],body=upload_data,headers = headerdata)
		else:
			conn.request(method="POST",url=requrl,body=upload_data,headers = headerdata)
		response = conn.getresponse()
		res = response.read()
		print (res)

		# 五分钟循环
		time.sleep(5)

def update_local_data():
	global state_pre

	conn = http.client.HTTPConnection("api.bmob.cn")
	# Bmob 请求头
	headerdata = {"X-Bmob-Application-Id":"ce623e8d32661f07cc185862722ca16f",
	"X-Bmob-REST-API-Key":"25d230fee5ced3b97d4ea6d11321eaae",
	"Content-Type": "application/json"}
	# 数据表的位置
	requrl = "/1/classes/KennelState"

	# 上传数据
	upload_data = {'kennelId':kennel_id,
	'kennelState':state_now}
	# 此处将数据转换成JSON格式才能提交，不然会返回107错误
	upload_data = json.dumps(upload_data)
	# 先查询
	conn.request(method="GET",url=requrl+'?where={"kennelId":"'+kennel_id+'"}',headers = headerdata)
	response = conn.getresponse()
	res = response.read()
	search_result = json.loads(res)
	if len(search_result['results']) and 'kennelState' in search_result['results'][0].keys():
		state_pre = search_result['results'][0]['kennelState']
		print('state_pre: ' + str(state_pre))

def analyse_sensor_data():
	global temperature
	global humidity
	global state_now
	global state_pre

	if (temperature < temperature_operation) and (state_pre != state_normal):
		state_now = state_normal
		send_sate_to_bmob()
	if (temperature >= temperature_operation):
		if (temperature < temperature_warn) and (state_pre != state_operation):
			state_now = state_operation
			send_sate_to_bmob()
		if (temperature >= temperature_warn) and (state_pre != state_warn):
			state_now = state_warn
			send_sate_to_bmob()
	state_pre = state_now

def send_sate_to_bmob():
	conn = http.client.HTTPConnection("api.bmob.cn")
	# Bmob 请求头
	headerdata = {"X-Bmob-Application-Id":"ce623e8d32661f07cc185862722ca16f",
	"X-Bmob-REST-API-Key":"25d230fee5ced3b97d4ea6d11321eaae",
	"Content-Type": "application/json"}
	# 数据表的位置
	requrl = "/1/classes/KennelState"

	# 上传数据
	upload_data = {'kennelId':kennel_id,
	'kennelState':state_now}
	# 此处将数据转换成JSON格式才能提交，不然会返回107错误
	upload_data = json.dumps(upload_data)
	# 先查询
	conn.request(method="GET",url=requrl+'?where={"kennelId":"'+kennel_id+'"}',headers = headerdata)
	response = conn.getresponse()
	res = response.read()
	search_result = json.loads(res)
	if len(search_result['results']):
		conn.request(method="PUT",url=requrl+'/'+search_result['results'][0]['objectId'],body=upload_data,headers = headerdata)
	else:
		conn.request(method="POST",url=requrl,body=upload_data,headers = headerdata)
	response = conn.getresponse()
	res = response.read()
	print ('state_now: ' + str(state_now))


if __name__ == "__main__":

	read_thread = thd.Thread(target = read_data_from_sensor)
	send_thread = thd.Thread(target = send_data_to_bmob)

	print('----------  starting the progress  ----------')
	print('----------  kennel_id: ' + kennel_id + '  ----------')
	print('----------  dht_pin: ' + str(dht_pin) + '  ----------')
	update_local_data()
	print()
	print()

	read_thread.start()
	send_thread.start()