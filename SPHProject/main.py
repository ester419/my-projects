import numpy as np
from kernel import Kernel
import random
import matplotlib.pyplot as plt
import math
from discrete import test_whole
from sympy import *

if __name__== "__main__" :
   x=[]
   optimal_n_list=[]
   y_cubic=[]
   y_parameter = []
   w_parameter = []
   w_cubic = []
   x_sin = []
   y_sin = []
   init = -3
   particle_init = -5
   particle_dis = particle_init
   span = 0.1
   range_1d=[particle_dis]
   dict = {}
   test = ()
   test_y = 0
   test_w = 0
   optimal_h = 0
   optimal_n = 0
   F2 = 0
   F3 = 0
   h=0.5
   optimal_w = []
   alpha_list = []
   optimal_w_list =[]
   F2_list = []
   F3_list = []
   x_w=[]
   F2_cubic = []
   F3_cubic = []
   upbound = 41
   derivative = 0
   d_list = []

#   for i in range(101):
#      range_1d.append(particle_init + i * 0.1)

#   range_1d.sort()



   for i in range(101):
      range_1d.append(particle_init + i*0.1 +random.random()*0.1)          #[-5,5]生成200个随机粒子
   range_1d.pop()
   range_1d.append(5)
   range_1d = [-5, -4.916110631840933, -4.893727829783245, -4.745741068530802, -4.656640424498534, -4.503855892414296, -4.431614121037023, -4.329019752824751, -4.281607197024888, -4.176674857791978, -4.0985997206804345, -3.9177161769105475, -3.8563429370566777, -3.7144845776768878, -3.6408210350782038, -3.5035624192686017, -3.492969926172621, -3.3474344689716826, -3.2948349802725514, -3.1550725882419965, -3.0701035596700037, -2.9433790679102536, -2.856494326273296, -2.78732316699827, -2.6744353958201597, -2.5750716024570783, -2.4936320692135894, -2.3454742698544337, -2.237630610214339, -2.1691456204326185, -2.041755536704583, -1.9046470718458202, -1.8168226480939145, -1.7078573694448147, -1.6274731392164112, -1.5335694362615913, -1.4541614526920545, -1.3285675708281184, -1.201517377608187, -1.1922531692063543, -1.0537109019769162, -0.9233084756225519, -0.8049994958759514, -0.7829240316483248, -0.6254522815552974, -0.5127868973448638, -0.4178043798758082, -0.3418534671747431, -0.2830561323530347, -0.1931918782994391, -0.06654732434295008, 0.02446553970732517, 0.19118293736391184, 0.29830300149807726, 0.3520688411613407, 0.42679282428416815, 0.5362523351745341, 0.6092461349957544, 0.7614207131472277, 0.8291130106595198, 0.9360710402396183, 1.0805431535653047, 1.10417870555963, 1.2820505589372007, 1.3755901605184586, 1.4218694600748063, 1.5647546268863077, 1.654946864027132, 1.7452848007560655, 1.8161623708574093, 1.9665287230692041, 2.0695438803269157, 2.169764747802377, 2.2138130181393305, 2.3197388700602546, 2.484266273772126, 2.5505859398714636, 2.667653211092491, 2.770595953818443, 2.8339096032898277, 2.9163032013034504, 3.076686026682048, 3.1981119940542353, 3.2231408034028592, 3.363309173433423, 3.455254257851171, 3.5531019828753347, 3.64255883344564, 3.746753621454277, 3.896780113826477, 3.985363246042362, 4.009741089923564, 4.163680221111728, 4.29900998297332, 4.36010946226636, 4.494514638567529, 4.5601717169720155, 4.625085433607875, 4.712645656963398, 4.846967780398891, 4.95328110684087, 5]
 #  for i in range(101):

 #     range_1d.append(particle_init + i*0.1+0.02)          #[-5,5]生成200个随机粒子

 #  range_1d.pop()



   print(range_1d)
   for j in range(0, upbound):
      x_sin.append(init + j * 0.1)
      y_sin.append((init + j * 0.1)**2)
#   print(range_1d)

#   for i in range(0, 100):
#      R = symbols('R')
#      alpha = ((complex)(2 * integrate(((sin(0.5 * pi * R) / (0.5 * pi * R))) ** (2+0.1*i), (R, 0, np.inf))).real)
#      print(2 + 0.1 * i)
#      print(alpha)
#      alpha_list.append(alpha)
#      print(alpha_list)

#   print(alpha_list)
   error_cubic_list = []
   error_parameter_list = []
   d = []
   d_cubic= []
   alpha_list = [2.0, 1.90625, 1.83984375, 1.76953125, 1.7109375, 1.657470703125, 1.613525390625, 1.577178955078125, 1.546966552734375, 1.521636962890625, 1.5, 1.4808998107910156, 1.4634170532226562, 1.4468345642089844, 1.4306449890136719, 1.4145393371582031, 1.3983557224273682, 1.3820679187774658, 1.3657283782958984, 1.3494415283203125, 1.3333333432674408, 1.3175281882286072, 1.3021335899829865, 1.2872308492660522, 1.272871483117342, 1.2590778581798077, 1.245846938341856, 1.2331556759309024, 1.220967176137492, 1.2092366502329241, 1.1979166666569654, 1.1869613130111247, 1.1763291406969074, 1.1659849186544307, 1.1559003378206398, 1.1460538780956995, 1.136430077283876, 1.1270184376626275, 1.1178121789125726, 1.1088070042605978, 1.1000000000058208, 1.0913887422611879, 1.0829706430158694, 1.0747425342924544, 1.0667004654205812, 1.058839673158218, 1.051154677947352, 1.0436394593909881, 1.0362876690151097, 1.029092846026117, 1.0220486111111127, 1.0151488225341154, 1.0083876871475184, 1.0017598257561247, 0.99526029727393, 0.9888845892788396, 0.9826285840733675, 0.9764885094960505, 0.9704608828611612, 0.9645424548962982, 0.9587301587301589, 0.9530210671315871, 0.947412359516056, 0.9419012988487658, 0.936485217547192, 0.9311615108294429, 0.9259276356359802, 0.9207811132127942, 0.9157195336122439, 0.9107405606698888, 0.9058419363839285, 0.9010214840022649, 0.8962771094686266, 0.8916068011664123, 0.8870086281131694, 0.8824807368974968, 0.8780213477197428, 0.873628749909757, 0.8693012972636556, 0.8650374034821009, 0.8608355379188712, 0.856694221772132, 0.8526120247805337, 0.8485875624278066, 0.8446194936158249, 0.8407065187378057, 0.8368473780691955, 0.833040850391578, 0.8292857517716455, 0.8255809344298404, 0.821925285648837, 0.8183177266881769, 0.8147572116862932, 0.8112427265436601, 0.8077732877902304, 0.8043479414465888, 0.8009657618915178, 0.7976258507494778, 0.7943273358104118, 0.791069369991954, 0.7878511303511303, 0.7846718171495093, 0.7815306529728465, 0.7784268819038483, 0.7753597687448679, 0.7723285982861947, 0.7693326746150405, 0.7663713204602887, 0.7634438765684078, 0.7605497011065362, 0.75768816908946, 0.7548586718279698, 0.7520606163967756, 0.749293425120757, 0.74655653507879, 0.7438493976247099, 0.7411714779251622, 0.7385222545141751, 0.735901218864276, 0.7333078749739214, 0.7307417389709057, 0.7282023387313173, 0.7256892135135139, 0.7232019136065074, 0.7207399999921031, 0.7183030440201078, 0.7158906270959211, 0.7135023403798467, 0.7111377844974945, 0.7087965692606932, 0.7064783133983786, 0.7041826442969822, 0.7019091977498846, 0.6996576177155462, 0.6974275560839616, 0.6952186724511134, 0.6930306339011248, 0.6908631147958255, 0.6887157965714573, 0.6865883675422604, 0.6844805227106814, 0.6823919635839549, 0.680322397996816, 0.6782715399401058, 0.6762391093950403, 0.6742248321729212, 0.6722284397600724, 0.6702496691677996, 0.6682882627871749, 0.6663439682484602, 0.6644165382849917, 0.6625057306013576, 0.6606113077457053, 0.6587330369860289, 0.6568706901902885, 0.6550240437102248, 0.6531928782687338, 0.6513769788506765, 0.6495761345970005, 0.6477901387020563, 0.6460187883139974, 0.6442618844381538, 0.6425192318432765, 0.640790638970552, 0.6390759178452917, 0.6373748839912031, 0.6356873563471521, 0.6340131571863358, 0.6323521120377775, 0.6307040496100709, 0.6290688017172934, 0.6274462032070179, 0.6258360918903534, 0.6242383084739455, 0.6226526964938744, 0.6210791022513868, 0.6195173747504009, 0.6179673656367308, 0.6164289291389702, 0.6149019220109853, 0.6133862034759648]
   print(len(alpha_list))
   for i in range(0,upbound):
       x.append(init+i*0.1)
       d.append(2*(init+i*0.1))
   for i in range(180):
      x_w.append(2 + 0.1 * i)
   for i in range(0,upbound):
       y_cubic.append(test_whole(h,3,init,span,i,2,range_1d,alpha_list)[0][1])
       w_cubic.append(test_whole(h,3,init,span,i,2,range_1d,alpha_list)[0][0])
       F2_cubic.append(test_whole(h,3,init,span,i,2,range_1d,alpha_list)[0][2])
       F3_cubic.append(test_whole(h, 3, init, span, i, 2, range_1d, alpha_list)[0][3])
       d_cubic.append(test_whole(h, 3, init, span, i, 2, range_1d, alpha_list)[0][4])

   for k in range(0, upbound):
      for i in range(100):

            test = test_whole(h, 2+i*0.1, init, span, k, 1, range_1d, alpha_list)
            test_y = test[0][1]
            test_w = test[0][0]
            F2 = test[0][2]
            F3 = test[0][3]
            derivative = test[0][4]
            w_parameter.append(abs(test_w-1))
            print(f"F2：{F2}")
            print(f"F3:{F3}")
            dict[abs(test_w-1)] = (test_y,2 + 0.1 * i,F2,F3, test_w, derivative)
      #      y_parameter.append(test_y)
#      error_cubic = math.sqrt(sum([(y_sin[i] - y_cubic[i]) ** 2 for i in range(0, upbound)]))
#      error_parameter = math.sqrt(sum([(y_sin[i] - y_parameter[i]) ** 2 for i in range(0, upbound)]))
#      error_cubic_list.append(error_cubic)
#      error_parameter_list.append(error_parameter)
      print(w_parameter)

      optimal_w = min(w_parameter)
      optimal_n = dict.get(optimal_w)[1]
      test = test_whole(h, optimal_n, init, span, k, 1, range_1d, alpha_list)
      optimal_n_list.append(optimal_n)
      optimal_w_list.append(dict.get(optimal_w)[4])
      y_parameter.append(dict.get(optimal_w)[0])
      F2_list.append(dict.get(optimal_w)[2])
      F3_list.append(dict.get(optimal_w)[3])
      d_list.append(dict.get(optimal_w)[5])
      dict = {}
      test = ()
      test_y = 0
      test_w = 0
      optimal_h = 0
      optimal_n = 0
      w_parameter = []
#      y_parameter = []

   x_parameter = [2+0.1*i for i in range(100)]

#   plt.figure()
#   plt.plot(x_w, error_parameter_list, linestyle='-.', color='green')
#   plt.plot(x_w, error_cubic_list, linestyle='-.', color='blue')
#   plt.show()

   print(optimal_w_list)
   print(f"W_cubic is{w_cubic}")
   print(optimal_n_list)


   error_cubic = math.sqrt(sum([(y_sin[i] - y_cubic[i])**2 for i in range(0,upbound)]))
   error_parameter = math.sqrt(sum([(y_sin[i] - y_parameter[i])**2 for i in range(0, upbound)]))
   print(f"样条函数误差 is {error_cubic}")
   print(f"参数函数误差 is {error_parameter}")
#   print(f"样条函数误差 is {y_cubic}")
#   print(f"参数函数误差 is {y_parameter}")
   error_parameter1 = math.sqrt(sum([(d_list[i] - d[i])**2 for i in range(0,upbound)]))
   error_cubic1 = math.sqrt(sum([(d_cubic[i] - d[i])**2 for i in range(0, upbound)]))
   print(f"样条导数误差 is {error_cubic1}")
   print(f"参数导数误差 is {error_parameter1}")
   print(range_1d)
   plt.figure()
   plt.plot(x, d_list, linestyle='-.', color='green')
   plt.plot(x, d, linestyle='-.', color='blue')
   plt.show()
   plt.figure()
   plt.plot(x, d_cubic, linestyle='-.', color='red')
   plt.plot(x, d, linestyle='-.', color='blue')
   plt.show()
   plt.figure()
   plt.plot(x, y_parameter, linestyle='-.', color='green')
   plt.plot(x_sin, y_sin, linestyle='-.', color='blue')
   plt.show()
#   for k in range(0,100):
#      x_.append(init+k*0.03)
#      y_.append(init+k*0.03)


   plt.figure()
   plt.plot(x, y_cubic, linestyle='--', color='red')
   plt.plot(x_sin, y_sin, linestyle='--', color='blue')

   plt.show()

   plt.figure()
   plt.plot(x, w_cubic, linestyle='--', color='red')
   plt.plot(x, optimal_w_list, linestyle='-.', color='green')
   print(optimal_w_list)
   plt.show()
   plt.figure()
   plt.plot(x, F2_cubic, linestyle='--', color='red')
   plt.plot(x, F2_list, linestyle='-.', color='green')
   plt.show()
   plt.figure()
   plt.plot(x, F3_cubic, linestyle='--', color='red')
   plt.plot(x, F3_list, linestyle='-.', color='green')
   plt.show()



