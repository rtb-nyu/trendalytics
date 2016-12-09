import tweepy
access_token = ""
access_token_secret = ""
consumer_key = ""
consumer_secret = ""
key = tweepy.OAuthHandler(consumer_key, consumer_secret)
key.set_access_token(access_token, access_token_secret)

outfile = 'recentTw.txt'
iterations = 10

def out2file(tweetsets):
    fout = open(outfile, 'a')
    for i in tweetsets:
        print(tweetsets[i]['localization'], end='\t', file=fout)
        print(tweetsets[i]['tweet'], file=fout)
    fout.close()

class tweet2stream(tweepy.StreamListener):
    output = {}
    def __init__(self, api=None):
        api = tweepy.API(key)
        self.api = api or API()
        self.n = 0
        self.m = 10
    def on_status(self, status):
        self.output[status.id] = {
        'tweet':status.text.encode('utf8'),
        'geo':status.geo,
        'localization':status.user.location}
        if self.output[status.id]['geo'] != None:
            self.n = self.n+1
        if self.n < self.m:
            return True
        else:
            return False

for i in range(0, iterations):
    stream = tweepy.streaming.Stream(key, tweet2stream())
    stream.filter(locations=[-74.1687,40.5722,-73.8062,40.9467])
    # Get longitude & latitude values for specific areas from:
    # http://boundingbox.klokantech.com
    
    tweetsets = tweet2stream().output
    out2file(tweetsets)
    print('Finished %d/%d iterations' % (i+1, iterations))
