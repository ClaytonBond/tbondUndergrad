def readData():
    # - Read in the data -----------------------------------------------------------------------------------------------
    filepath = 'test.txt'
    arrayTest = []
    with open(filepath) as fp:
        print 'Loading in data...'
        line = fp.readline()
        cnt = 1
        while line:
            arrayTest.append(line.split(', '))
            line = fp.readline()
            cnt += 1

    for item in arrayTest:
        del arrayTest[0]

    sentences = arrayTest
    sentences.append('Tensorflow'.lower().split(' '))  # This is your search term
    return sentences
