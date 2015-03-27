module LastFmImporter 

open Chronicle
open FSharp.Data
open HttpClient

// Build our types based on samples of the JSON we expect.
type LastFmApiCredentials = JsonProvider<"data/lastfm/credentials.sample.json">
type LastFmTopTracks = JsonProvider<"data/lastfm/user.getTopTracks.json">
type LastFmRecentTracks = JsonProvider<"data/lastfm/user.getRecentTracks.json">
type LastFmUser = JsonProvider<"data/lastfm/user.getInfo.json">

// You need to provide this file with a valid API key for this to work
let apiKey =
    LastFmApiCredentials.Load(
        __SOURCE_DIRECTORY__ + "/data/lastfm/credentials.json").ApiKey

// Helper for building requests:
type RequestBuilder(username: string) =
    let baseUri = "http://ws.audioscrobbler.com/2.0/"

    let baseRequest =
        createRequest Get baseUri
        |> withQueryStringItem { name="api_key"; value=apiKey }  
        |> withQueryStringItem { name="format"; value="json" }  
        |> withQueryStringItem { name="user"; value=username }

    let forMethod (methodName: string):Request =
        baseRequest 
        |> withQueryStringItem { name="method"; value=methodName }  

    member m.forUserInfo = forMethod "user.getInfo"
    member m.forTopTracks = forMethod "user.getTopTracks"
    member m.forRecentTracks = forMethod "user.getRecentTracks"

// Gets some user information from the Last.fm API
let getUser(username: string):Async<User> = async {
    let request = RequestBuilder(username).forUserInfo
    let! response = (request |> getResponseBodyAsync)
    let json = LastFmUser.Parse(response)
    return User(json.User.Name, json.User.Playcount)
}

// Gets the user's top tracks
let getTopTracksForUser(username: string) = async {
    let request = RequestBuilder(username).forTopTracks
    let! response = (request |> getResponseBodyAsync)
    let tracks = LastFmTopTracks.Parse(response)
    return seq {
        for track in tracks.Toptracks.Track do
            let artist = new Artist(track.Artist.Name, track.Artist.Mbid)
            yield new Track(track.Name, artist, track.Mbid)
    }
}

let maxTracksPerPage = 200
let pagesNeededFor (total:int) =
    let dividesEvenly = (total % maxTracksPerPage) = 0
    if dividesEvenly
    then total / maxTracksPerPage
    else total / maxTracksPerPage + 1
   
let buildPaginatedRequestsFor(username: string) = async {
    let! user = getUser(username)
    let pagesNeeded = pagesNeededFor (user.TrackCount)
    let request = RequestBuilder(username).forRecentTracks
    return seq {
        for i in 1 .. pagesNeeded do
            yield (request 
                   |> withQueryStringItem { name="page"; 
                                            value=i.ToString() }
                   |> withQueryStringItem { name="limit"; 
                                            value=maxTracksPerPage.ToString() })
    }
}

let parseRecentTracksResponse (response: string) =
    let tracks = LastFmRecentTracks.Parse(response)
    seq {
        for track in tracks.Recenttracks.Track do
            let artist = new Artist(track.Artist.Text, track.Artist.Mbid)
            yield new Track(track.Name, artist, track.Mbid)
    }

let getResponses requests= seq {
    for request in requests do
        yield request |> getResponseBody
        // Dumb way to slow this down so Last.FM stops rate-limiting me.
        System.Threading.Thread.Sleep(250)
}

// Gets all the user's tracks!
let getAllTracksForUser(username: string) = async {
    let! requests = buildPaginatedRequestsFor username
    return getResponses requests
    |> Seq.map parseRecentTracksResponse
    |> Seq.concat
}
