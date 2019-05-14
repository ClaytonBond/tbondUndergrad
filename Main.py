import subprocess
import Kmeans
import FileIO
import NB
import Random

# Call custom java html tokenizer
subprocess.call(['java', '-jar', 'PreProcessing.jar'])

data = FileIO.readData()
groups, sentences = Kmeans.run(data)
NB.run(groups, sentences)
groups, sentences = Random.run(data)
NB.run(groups, sentences)


""" 
Use this for quick run testing

sentences = [['this', 'is', 'the', 'one','good', 'machine', 'learning', 'book'],
            ['this', 'is',  'another', 'book'],
            ['one', 'more', 'book'],
            ['weather', 'rain', 'snow'],
            ['yesterday', 'weather', 'snow'],
            ['forecast', 'tomorrow', 'rain', 'snow'],
            ['this', 'is', 'the', 'new', 'post'],
            ['this', 'is', 'about', 'more', 'machine', 'learning', 'post'],
            ['and', 'this', 'is', 'the', 'one', 'last', 'post', 'book']]

groups = [0,0,0,1,1,1,0,0,0]
"""

