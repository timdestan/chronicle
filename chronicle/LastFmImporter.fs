module LastFmImporter 

open Chronicle
open FSharp.Data
open HttpClient
open System.IO
open Time

module Api =
    // A file containing the API key and secret
    type Credentials = JsonProvider<"data/lastfm/credentials.sample.json">
    // Types for the responses from the various API calls.
    type TopTracks = JsonProvider<"data/lastfm/user.getTopTracks.json">
    type RecentTracks = JsonProvider<"data/lastfm/user.getRecentTracks.json">
    type User = JsonProvider<"data/lastfm/user.getInfo.json">

// You need to provide this file with a valid API key for this to work
let apiKey =
    Api.Credentials.Load(
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
    let json = Api.User.Parse(response)
    return {
        User.name = json.User.Name;
        trackCount = json.User.Playcount
    }
}

// Gets the user's top tracks
let getTopTracksForUser(username: string) = async {
    let request = RequestBuilder(username).forTopTracks
    let! response = (request |> getResponseBodyAsync)
    let tracks = Api.TopTracks.Parse(response)
    return seq {
        for track in tracks.Toptracks.Track do
            let artist = {
                Artist.name = track.Artist.Name;
                mbid = track.Artist.Mbid
            }
            yield {
                Track.name = track.Name;
                artist = artist;
                mbid = track.Mbid
            }
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
    let pagesNeeded = pagesNeededFor user.trackCount
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
    let tracks = Api.RecentTracks.Parse(response)
    seq {
        for jsonTrack in tracks.Recenttracks.Track do
            let artist = { 
                Artist.name = jsonTrack.Artist.Text; 
                mbid = jsonTrack.Artist.Mbid 
            }
            let track = {
                Track.name = jsonTrack.Name;
                artist = artist;
                mbid = jsonTrack.Mbid
            }
            yield {
                time = Time.fromUnixTime(jsonTrack.Date.Uts |> int64);
                value = track
            }
    }

// Get all responses synchronously, one after the other.
let getResponses requests = seq {
    for request in requests do
        yield request |> getResponseBody
}

let getAllListensForUser(username: string) = async {
    let! requests = buildPaginatedRequestsFor username
    return getResponses requests
    |> Seq.map parseRecentTracksResponse
    |> Seq.concat
}

